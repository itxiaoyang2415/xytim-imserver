package com.bx.implatform.task.schedule;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bx.implatform.annotation.RedisLock;
import com.bx.implatform.config.props.MinioProperties;
import com.bx.implatform.contant.RedisKey;
import com.bx.implatform.entity.FileInfo;
import com.bx.implatform.entity.Group;
import com.bx.implatform.entity.User;
import com.bx.implatform.service.FileService;
import com.bx.implatform.service.GroupService;
import com.bx.implatform.service.UserService;
import com.bx.implatform.thirdparty.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 清理无效的永久文件
 * 说明：
 * 1.因为用户可以重复上传头像，而头像作为永久文件，不会过期，本任务的目的是删除没有被使用的文件，释放磁盘空间
 * 2.本任务平时不开启，如果磁盘已满，短时间又无法扩容，可以考虑执行本任务进行清理
 * 3.以下情况不适合开启本任务:
 *  (1)用户量大于50w
 *  (2)群数量大于50w
 *  (3)除了头像以外，还有其他地方使用了永久文件
 * 4.在开启本任务前,请先进行测试，否则文件一旦删错，后果很严重！！！
 *
 *
 * @author Blue
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileInvalidTask {

    private final FileService fileService;
    private final MinioService minioService;
    private final MinioProperties minioProps;
    private final UserService userService;
    private final GroupService groupService;


    @RedisLock(prefixKey = RedisKey.IM_LOCK_FILE_INVALID_TASK)
   // @Scheduled(cron = "0 0 3 * * ?")
    public void run() {
        log.info("【定时任务】无效文件处理...");
        int delSize = 0;
        int batchSize = 100;
        Long minId = 0L;
        List<FileInfo> files = loadBatch(minId,batchSize);
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.ne(User::getHeadImage,"");
        wrapper.select(User::getHeadImage);
        List<User> users = userService.list(wrapper);
        List<String> userImages = users.stream().map(User::getHeadImage).collect(Collectors.toList());
        LambdaQueryWrapper<Group> wrapper2 = Wrappers.lambdaQuery();
        wrapper2.ne(Group::getHeadImage,"");
        wrapper2.select(Group::getHeadImage);
        List<Group> groups = groupService.list(wrapper2);
        List<String> groupImages = groups.stream().map(Group::getHeadImage).collect(Collectors.toList());
        while (true) {
            for (FileInfo fileInfo : files) {
                String url = fileInfo.getFilePath();
                if(userImages.contains(url)){
                    continue;
                }
                if(groupImages.contains(url)){
                    continue;
                }
                String relativePath = url.substring(fileInfo.getFilePath().indexOf(minioProps.getBucketName()));
                String[] arr = relativePath.split("/");
                String bucket = minioProps.getBucketName();
                String path = arr[1];
                String fileNme = StrUtil.join("/", arr[2], arr[3]);
                if (minioService.isExist(bucket, path, fileNme)) {
                    if (!minioService.remove(bucket, path, fileNme)) {
                        // 删除失败，不再往下执行
                        log.error("删除过期文件异常, id:{},文件名:{}", fileInfo.getId(), fileInfo.getFileName());
                        return;
                    }
                    // 删除文件信息
                    fileService.removeById(fileInfo.getId());
                    delSize++;
                    log.info("删除第{}个文件",delSize);
                }
            }
            if (files.size() < batchSize) {
                break;
            }
            // 下一批
            minId = files.get(files.size()-1).getId();
            files = loadBatch(minId,batchSize);
        }
        log.info("无效文件处理完成，共清理{}个文件",delSize);
    }

    List<FileInfo> loadBatch(Long minId,int size) {
        Date minDate = DateUtils.addDays(new Date(), -minioProps.getExpireIn());
        LambdaQueryWrapper<FileInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FileInfo::getIsPermanent, true);
        wrapper.le(FileInfo::getUploadTime, minDate);
        wrapper.ge(FileInfo::getId,minId);
        wrapper.orderByAsc(FileInfo::getId);
        wrapper.last("limit " + size);
        return fileService.list(wrapper);
    }
}
