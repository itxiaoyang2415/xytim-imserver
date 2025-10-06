<template>
	<div class="chat-video">
		<audio ref="callAudio" loop="true" x5-playsinline playsinline webkit-playsinline>
			<source src="@/assets/audio/call.wav">
		</audio>
		<div class="call-box" v-show="!isReady&&!isHost">
			<div class="inv-avatar" v-if="inviter">
				<head-image :url="inviter.headImage" :name="inviter.nickName" :size="150" :isReady="isJSBridgeReady">
					<div class="inv-name">{{inviter.nickName}}</div>
				</head-image>
			</div>
			<div class="inv-text">邀请你加入多人通话</div>
			<div class="user-list-text">参与通话的还有:</div>
			<div class="user-list">
				<div v-for="user in userInfos" v-show="user.id!=inviterId" :key="user.id">
					<head-image :url="user.headImage" :name="user.nickName" :size="40" :isReady="isJSBridgeReady">
					</head-image>
				</div>
			</div>
		</div>
		<div class="video-box" v-show="isReady||isHost">
			<div v-show="!isLeaderMode" id="videos" class="video-list-1">
				<div :style="toVideoStyle(user)" v-for="user in userInfos" :key="user.id" :id="'video'+user.id"
					@click="onClickVideo(user)">
					<local-video v-if="user.id==mine.id" ref="localVideo" :userInfo="mine" :width="toVw(user)"
						:height="toVh(user)" :isJSBridgeReady="isJSBridgeReady"
						@switchFacing="onSwitchFacing"></local-video>
					<remote-video v-if="user.id!=mine.id" :ref="'remoteVideo'+user.id" :userInfo="user"
						:groupId="groupId" :width="toVw(user)" :height="toVh(user)"
						:isJSBridgeReady="isJSBridgeReady"></remote-video>
				</div>
				<div v-show="userInfos.length<config.maxChannel" id="invite1" :style="videoStyle">
					<invite-member ref="invMember" :API="API" :groupId="groupId" :userInfos="userInfos"
						:maxChannel="config.maxChannel" :isJSBridgeReady="isJSBridgeReady"
						@ok="appendUser"></invite-member>
				</div>
			</div>
			<div v-show="isLeaderMode" class="video-list-2">
				<div id="leader" class="leader"></div>
				<div id="follower" class="follower">
					<div v-show="userInfos.length<config.maxChannel" id="invite2" :style="videoStyle">
						<invite-member ref="invMember" :API="API" :groupId="groupId" :userInfos="userInfos"
							:maxChannel="config.maxChannel" :isJSBridgeReady="isJSBridgeReady"
							@ok="appendUser"></invite-member>
					</div>
				</div>
			</div>
		</div>
		<div class="control-bar" ref="refCtrl" v-show="isReady||isHost">
			<div class="chat-time">{{chatTimeString}}</div>
			<div class="dev-bar">
				<div class="icon-box" v-show="isReady && isMicroPhone">
					<div class="icon iconfont icon-microphone-on icon-front" @click="onSwitchMicroPhone()"></div>
					<div class="icon-text">麦克风已开</div>
				</div>
				<div class="icon-box" v-show="isReady && !isMicroPhone">
					<div class="icon iconfont icon-microphone-off icon-back" @click="onSwitchMicroPhone()"></div>
					<div class="icon-text">麦克风已关</div>
				</div>
				<div class="icon-box" v-show="isReady && isSpeaker">
					<div class="icon iconfont icon-speaker-on icon-front" @click="onSwitchSpeaker()"></div>
					<div class="icon-text">扬声器已开</div>
				</div>
				<div class="icon-box" v-show="isReady && !isSpeaker">
					<div class="icon iconfont icon-speaker-off icon-back" @click="onSwitchSpeaker()"></div>
					<div class="icon-text">扬声器已关</div>
				</div>
				<div class="icon-box" v-show="isReady && isCamera">
					<div class="icon iconfont icon-camera-on icon-front" @click="onSwitchCamera()"></div>
					<div class="icon-text">摄像头已开</div>
				</div>
				<div class="icon-box" v-show="isReady && !isCamera">
					<div class="icon iconfont icon-camera-off icon-back" @click="onSwitchCamera()"></div>
					<div class="icon-text">摄像头已关</div>
				</div>
			</div>
		</div>
		<div class="bottom-bar" ref="refBot">
			<div v-show="!isHost && !isChating" class="icon iconfont icon-phone-reject red" @click="onReject()">
			</div>
			<div v-show="!isHost && !isChating" class="icon iconfont icon-phone-accept " @click="onAccept()">
			</div>
			<div v-show="isHost && !isChating" class="icon iconfont icon-phone-reject red" @click="onCancel()">
			</div>
			<div v-show="isChating" class="icon iconfont icon-phone-reject red" @click="onQuit()">
			</div>
		</div>
	</div>
</template>

<script>
import ImWebRtc from '@/common/webrtc'
import ImCamera from '@/common/camera'
import ImApi from '@/common/api'
import UniEvent from '@/common/uniEvent'
import HeadImage from '@/components/HeadImage'
import LocalVideo from '@/components/LocalVideo'
import RemoteVideo from '@/components/RemoteVideo'
import InviteMember from '@/components/InviteMember'

export default {
	data() {
		return {
			env: {},
			isJSBridgeReady: false,
			camera: new ImCamera(), // 摄像头和麦克风
			webrtc: new ImWebRtc(), // webrtc相关
			uniEvent: new UniEvent(), // 接受来自uniapp的消息
			API: null, // API接口请求对象
			stream: null, // 本地视频流
			isMicroPhone: true, // 是否开启麦克风
			isFacing: true, // 是否前置摄像头
			isSpeaker: true, // 是否开启扬声器
			isCamera: false, // 是否开启视频
			chatTime: 0, // 聊天时长
			chatTimer: null, // 聊天计时器
			heartbeatTimer: null, // 心跳定时器
			tipTimer: null, // 提示语定时器
			lastTipTime: null, // 上一次提示时间
			state: "INIT", // INIT:初始状态  WAITING:等待呼叫或接听 CHATING:聊天中  CLOSE:关闭 ERROR:出现异常
			baseUrl: "", // 服务器基础接口路径
			loginInfo: {}, // token信息
			groupId: null, // 群id
			userId: null, // 当前用户id
			inviterId: null, // 发起邀请的用户id
			leaderId: null, // leader id (大屏显示)
			isHost: false, // 
			userInfos: [], // 用户列表，
			config: {}, // webrtc配置
			vw: 150, // 视频窗口宽度
			vh: 150, // 视频窗口高度
			leaderVw: 600, // leader窗口宽度
			leaderVh: 600, // leader窗口高度
		};
	},
	components: {
		HeadImage,
		LocalVideo,
		RemoteVideo,
		InviteMember
	},
	methods: {
		onNavBack() {
			// 用户按下了左上角回退键，强制退出
			if (this.isClose) {
				return;
			}
			if (this.isHost && !this.isChating) {
				// 强制终止呼叫
				console.log("强制终止呼叫");
				this.API.cancel(this.groupId);
			} else if (!this.isHost && this.state == "WAITING") {
				// 强制拒绝接听
				console.log("强制拒绝接听");
				this.API.reject(this.groupId);
			} else if (this.isChating) {
				// 强制退出通话
				console.log("强制退出通话");
				this.API.quit(this.groupId);
			}
		},
		onClickVideo(userInfo) {
			console.log("onClickVideo")
			if (userInfo.id == this.leaderId) {
				// 退出leader模式
				this.leaderId = null;
			} else {
				// 进入leader模式
				this.leaderId = userInfo.id;
			}
			this.refreshVideoWH();
			this.reLayoutVideo();
		},
		onSwitchMicroPhone() {
			this.isMicroPhone = !this.isMicroPhone;
			this.$refs.localVideo[0].setMicroPhone(this.isMicroPhone);
			// 通知其他人我开/关了麦克风
			this.API.device(this.groupId, this.isCamera, this.isMicroPhone, false);
			console.log("麦克风:" + this.isMicroPhone)
		},
		onSwitchFacing(isFacing) {
			this.isFacing = isFacing;
			this.openStream().then(() => {
				// 保持麦克风状态
				this.$refs.localVideo[0].setMicroPhone(this.isMicroPhone);
				// 切换传输的视频流
				this.userInfos.forEach(user => {
					if (user.id != this.mine.id) {
						const refName = 'remoteVideo' + user.id;
						this.$refs[refName][0].switchStream(this.stream);
					}
				})
				const cameaName = isFacing ? "前置" : "后置"
				console.log("摄像头翻转:" + cameaName);
			}).catch((e) => {
				this.showToast("摄像头翻转失败")
				console.log("摄像头翻转失败:" + e.message)
			})
		},
		onSwitchCamera() {
			this.isCamera = !this.isCamera;
			this.mine.isCamera = this.isCamera;
			// 重新打开设备
			this.openStream().then(() => {
				// 保持麦克风状态
				this.$refs.localVideo[0].setMicroPhone(this.isMicroPhone);
				// 切换传输的视频流
				this.userInfos.forEach(user => {
					if (user.id != this.mine.id) {
						const refName = 'remoteVideo' + user.id;
						// 开关摄像头需要重新连接
						this.$refs[refName][0].reconnect(this.stream);
					}
				})
				// 通知其他人我开/关了摄像头
				this.API.device(this.groupId, this.isCamera, this.isMicroPhone, false);
				console.log("摄像头:" + this.isCamera);
			})
		},
		onSwitchSpeaker() {
			this.isSpeaker = !this.isSpeaker;
			// 切换传输的视频流
			this.userInfos.forEach(user => {
				if (user.id != this.mine.id) {
					const refName = 'remoteVideo' + user.id;
					this.$refs[refName][0].setMute(!this.isSpeaker);
				}
			})
			console.log("扬声器切换:" + this.isSpeaker);
		},
		onRTCMessage(msg) {
			// 除了发起通话，如果在关闭状态就无需处理
			if (msg.type != this.$enums.MESSAGE_TYPE.RTC_GROUP_SETUP &&
				this.state == 'CLOSE') {
				return;
			}
			// RTC信令处理
			switch (msg.type) {
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_SETUP:
					this.onRTCSetup(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_ACCEPT:
					this.onRTCAccept(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_REJECT:
					this.onRTCReject(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_JOIN:
					this.onRTCJoin(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_FAILED:
					this.onRTCFailed(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_CANCEL:
					this.onRTCCancel(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_QUIT:
					this.onRTCQuit(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_INVITE:
					this.onRTCInvite(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_OFFER:
					this.onRTCOffer(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_ANSWER:
					this.onRTCAnswer(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_CANDIDATE:
					this.onRTCCandidate(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_GROUP_DEVICE:
					this.onRTCDevice(msg)
					break;

			}
		},
		onRTCSetup(msg) {
			// 呼叫铃声
			this.$refs.callAudio.play();
		},
		onRTCAccept(msg) {
			if (msg.selfSend) {
				// 我在其他终端接受了的通话
				this.close("已在其他设备接听");
				return;
			}
			const remoteUserId = msg.sendId;
			if (this.isChating) {
				// 与用户发起视频连接
				const refName = 'remoteVideo' + remoteUserId;
				this.$refs[refName][0].connect();
			} else if (this.isHost) {
				// 与用户发起视频连接
				const refName = 'remoteVideo' + remoteUserId;
				this.$refs[refName][0].connect();
				// 只有有人进来就进入聊天状态
				this.state = 'CHATING';
				// 停止呼叫铃声
				this.$refs.callAudio.pause();
				// 开始计时
				this.startChatTime();
			}
		},
		onRTCReject(msg) {
			if (msg.selfSend) {
				// 我在其他终端拒绝了的通话
				this.close("已在其他设备拒绝");
				return;
			}
			// 有人拒绝了通话
			const remoteUserId = msg.sendId;
			// 移除用户
			this.removeUser(remoteUserId, "未进入通话")
		},
		onRTCFailed(msg) {
			// 有人无法加入通话
			const remoteUserId = msg.sendId;
			const failedInfo = JSON.parse(msg.content);
			if (failedInfo.userIds.find(userId => userId == this.mine.id)) {
				this.close("您未接听");
				return;
			}
			let firUserId = failedInfo.userIds[0];
			let firUserName = this.userInfos.find(u => u.id == firUserId).nickName;
			let tip = `'${firUserName}'`
			if (failedInfo.userIds.length > 1) {
				tip += `等${failedInfo.userIds.length}人`;
			}
			tip += `未能进入通话,原因:${failedInfo.reason}`;
			this.showToast(tip)
			// 移除用户
			failedInfo.userIds.forEach((userId) => this.removeUser(userId));
		},
		onRTCJoin(msg) {
			// 有用户进入了通话
			const remoteUserId = msg.sendId;
			let userInfo = JSON.parse(msg.content);
			if (!this.isExist(remoteUserId)) {
				this.userInfos.push(userInfo);
			}
			this.$nextTick(() => {
				// 初始化视频窗口
				this.initUserVideo();
				// 与用户发起视频连接
				const refName = 'remoteVideo' + remoteUserId;
				this.$refs[refName][0].connect();
			})
			// 只有有人进来就进入聊天状态
			this.state = 'CHATING';
			// 停止呼叫铃声
			this.$refs.callAudio.pause();
			// 开始计时
			this.startChatTime();
		},
		onRTCOffer(msg) {
			// 有用户向往我发起视频连接
			const remoteUserId = msg.sendId;
			const offer = JSON.parse(msg.content)
			// 回复对方
			const refName = 'remoteVideo' + remoteUserId;
			this.$refs[refName][0].onOffer(offer);
		},
		onRTCAnswer(msg) {
			// 对方同意了我的视频连接请求
			const remoteUserId = msg.sendId;
			const answer = JSON.parse(msg.content);
			// 设置answer信息
			const refName = 'remoteVideo' + remoteUserId;
			this.$refs[refName][0].onAnswer(answer);
		},
		onRTCCancel() {
			// 通话取消，直接退出
			this.close("通话已取消");
		},
		onRTCQuit(msg) {
			// 有人退出了通话
			const remoteUserId = msg.sendId;
			// 移除该用户
			this.removeUser(remoteUserId, "已退出通话");
		},
		onRTCInvite(msg) {
			// 有用户被邀请进来
			let userInfos = JSON.parse(msg.content);
			// 加入列表
			this.appendUser(userInfos);
		},
		onRTCCandidate(msg) {
			// 对方同意了我的视频连接请求
			const remoteUserId = msg.sendId;
			const candidate = JSON.parse(msg.content);
			// 设置answer信息
			const refName = 'remoteVideo' + remoteUserId;
			this.$refs[refName][0].setCandidate(candidate);
		},
		onRTCDevice(msg) {
			// 对方进行了设备操作
			const remoteUserId = msg.sendId;
			const devInfo = JSON.parse(msg.content);
			// 记录摄像头/麦克风开关标志
			let userInfo = this.userInfos.find(u => u.id == remoteUserId);
			userInfo.isCamera = devInfo.isCamera;
			userInfo.isMicroPhone = devInfo.isMicroPhone;
			userInfo.isShareScreen = devInfo.isShareScreen;
		},
		onAccept() {
			if (!this.checkDevEnable()) {
				this.API.failed(this.groupId, "设备不支持通话")
				this.close();
				return;
			}
			// 进入房间
			this.state = "CHATING";
			// 停止呼叫铃声
			this.$refs.callAudio.pause();
			// 刷新窗口大小
			this.refreshVideoWH();
			// 打开摄像头
			this.openStream().finally(() => {
				// 初始化视频窗口
				this.initUserVideo();
				// 加入通话
				this.API.accept(this.groupId).then(() => {
					// 聊天时长
					this.startChatTime();
				}).catch(() => {
					this.close();
				})
			})
		},
		onReject() {
			// 退出通话
			this.API.reject(this.groupId);
			// 退出
			this.close("您拒绝了通话");
		},
		onQuit() {
			// 退出通话
			this.API.quit(this.groupId);
			// 退出
			this.close("退出通话");
		},
		onCancel() {
			// 取消通话
			this.API.cancel(this.groupId);
			// 退出
			this.close("您取消通话");
		},
		onSetup() {
			if (!this.checkDevEnable()) {
				this.close();
			}
			// 打开摄像头
			this.openStream().finally(() => {
				// 不管是否成功都进行初始化
				this.initUserVideo()
				// 发起呼叫
				this.API.setup(this.groupId, this.userInfos).then(() => {
					// 准备状态
					this.state = "READY";
					// 播放呼叫
					this.$refs.callAudio.play();
				}).catch((e) => {
					this.close();
				});
			})
		},
		onJoin() {
			if (!this.checkDevEnable()) {
				this.close();
			}
			// 就绪
			this.state = "READY";
			// 刷新窗口大小
			this.refreshVideoWH();
			// 打开摄像头
			this.openStream().finally(() => {
				// 不管是否成功都进行初始化
				this.initUserVideo()
				// 发起加入请求
				this.API.join(this.groupId).then(() => {
					// 聊天状态
					this.state = "CHATING";
					// 开始计时
					this.startChatTime();
				}).catch((e) => {
					this.close();
				});
			})
		},
		onInviteMember() {
			this.$refs.invMember.open();
		},
		appendUser(userInfos) {
			userInfos.forEach(user => {
				if (!this.isExist(user.id)) {
					this.userInfos.push(user);
					console.log(`'${user.nickName}'加入通话`);
				}
			})
			// 初始化视频窗口
			this.$nextTick(() => {
				this.initUserVideo();
			})
		},
		isExist(userId) {
			return !!this.userInfos.find(u => u.id == userId);
		},
		removeUser(userId, tip) {
			if (!this.isExist(userId)) {
				return;
			}
			// 销毁资源
			const refName = 'remoteVideo' + userId;
			if (this.$refs[refName]) {
				this.$refs[refName][0].close();
			}
			// 提示语
			const idx = this.userInfos.findIndex(user => user.id == userId);
			const userInfo = this.userInfos[idx];
			// 移除用户
			this.userInfos.splice(idx, 1);
			if (this.isHost && tip) {
				this.showToast(`'${userInfo.nickName}'${tip}`);
			}
			// 如果只剩下自己了，则自己也退出
			if (this.userInfos.length <= 1) {
				this.onQuit();
			}
			// leader退出，恢复到正常模式
			if (this.isLeader(userInfo)) {
				this.leaderId = null;
				this.refreshVideoWH();
				this.reLayoutVideo();
			}
		},
		openStream() {
			return new Promise((resolve, reject) => {
				if (this.isCamera) {
					// 打开摄像头+麦克风
					this.camera.openVideo(this.isFacing).then((stream) => {
						console.log("摄像头打开成功")
						this.stream = stream;
						// 显示本地视频
						this.$refs.localVideo[0].open(stream);
						resolve(stream);
					}).catch((e) => {
						this.showToast(e.message)
						console.log("本地摄像头打开失败:" + e.message)
						reject(e);
					})
				} else {
					// 打开麦克风
					this.camera.openAudio().then((stream) => {
						console.log("麦克风打开成功")
						this.stream = stream;
						// 显示本地视频
						this.$refs.localVideo[0].open(stream);
						resolve(stream);
					}).catch((e) => {
						this.showToast(e.message)
						console.log("本地麦克风打开失败:" + e.message)
						reject(e);
					})
				}
			})
		},
		initUserVideo() {
			// 初始化视频窗口
			this.userInfos.forEach(user => {
				if (user.id != this.userId) {
					const refName = 'remoteVideo' + user.id
					// 防止重复初始化
					if (!this.$refs[refName][0].isInit) {
						const configuration = {
							iceServers: this.config.iceServers
						};
						this.$refs[refName][0].init(this.API, configuration, this.stream)
					}
				}
			})
		},
		startChatTime() {
			if (!this.chatTimer) {
				this.chatTime = 0;
				this.chatTimer = setInterval(() => {
					this.chatTime++;
				}, 1000)
			}
		},
		startHeartBeat() {
			// 每15s推送一次心跳
			this.heartbeatTimer && clearInterval(this.heartbeatTimer);
			this.heartbeatTimer = setInterval(() => {
				this.API.heartbeat(this.groupId);
			}, 15000)
		},
		checkDevEnable() {
			// 检测摄像头
			if (!this.camera.isEnable()) {
				this.showToast("访问摄像头失败");
				return false;
			}
			// 检测webrtc
			if (!this.webrtc.isEnable()) {
				this.showToast("初始化RTC失败，原因可能是: 1.服务器缺少ssl证书 2.您的设备不支持WebRTC");
				return false;
			}
			return true;
		},
		showToast(tip) {
			if (!this.lastTipTime || new Date().getTime() - this.lastTipTime > 1000) {
				this.$toast(tip);
				this.lastTipTime = new Date().getTime();
				console.log(tip);
			} else {
				// 当前已有提示，1s之后再弹出
				setTimeout(() => this.showToast(tip), 1000)
			}
		},
		reLayoutVideo() {
			// 移动视频窗口
			console.log("reLayoutVideo")
			const leader = document.getElementById("leader");
			const invite1 = document.getElementById("invite1");
			const invite2 = document.getElementById("invite2");
			this.userInfos.forEach((userInfo) => {
				const id = 'video' + userInfo.id;
				const video = document.getElementById(id);
				if (!this.isLeaderMode) {
					invite1.before(video)
				} else if (this.leaderId == userInfo.id) {
					leader.appendChild(video);
				} else {
					invite2.before(video);
				}
			})
		},
		refreshVideoWH() {
			let w = window.innerWidth;
			let h = window.innerHeight;
			console.log("屏幕大小", w, h)
			if (!w || !h) {
				// 未能取到屏幕大小,延迟100ms再处理
				setTimeout(() => this.refreshVideoWH(), 100);
				return;
			}
			let count = this.userInfos.length;
			if (!w || !h || !count) {
				return;
			}
			// 减去底下操作栏空间
			h = h - 520 * w / 750;
			// 加上邀请按钮的数量
			if (count < this.config.maxChannel) {
				count = count + 1;
			}
			if (this.isLeaderMode) {
				this.vw = w / 3;
				this.vh = w / 3;
				this.leaderVw = w;
				this.leaderVh = h - this.vh;
				// 长宽比不能超过1:1.2
				if (this.leaderVh > w * 1.2) {
					this.leaderVh = w * 1.2;
				}
			} else {
				// 单行视频数量
				let row = Math.ceil(Math.sqrt(count));
				let col = Math.ceil(count / row);
				this.vw = w / row;
				this.vh = h / col;
				// 长宽比不能超过1:1.2
				if (this.vh > this.vw * 1.2) {
					this.vh = this.vw * 1.2
				}
			}
			console.log("视频大小", this.vw, this.vh)
		},
		toVw(userInfo) {
			return this.isLeader(userInfo) ? this.leaderVw : this.vw;
		},
		toVh(userInfo) {
			return this.isLeader(userInfo) ? this.leaderVh : this.vh;
		},
		toVideoStyle(userInfo) {
			return this.isLeader(userInfo) ? this.leaderStyle : this.videoStyle;
		},
		isLeader(userInfo) {
			return this.leaderId == userInfo.id;
		},
		initEvent() {
			this.uniEvent.listen((key, data) => {
				console.log("来自app的消息：" + key + ":" + JSON.stringify(data));
				if (key == "RTC_MESSAGE") {
					// RTC信令
					this.onRTCMessage(data);
				} else if (key == "NAV_BACK") {
					this.onNavBack();
				}
			})
		},
		init() {
			// 创建API对象
			this.API = new ImApi(this.baseUrl, this.loginInfo);
			// 等待用户接受呼叫
			this.state = "WAITING";
			// 如果我是发起人，发起群通话
			if (this.isHost) {
				this.onSetup();
			} else if (this.inviterId == this.userId) {
				// 自己邀请自己，表示自己主动加入通话
				this.onJoin();
			}
			// 启动心跳
			this.startHeartBeat();
		},
		close(tip) {
			// 弹出关闭提示语
			tip && this.showToast(tip);
			// 清理定时器
			this.chatTimer && clearInterval(this.chatTimer);
			this.heartbeatTimer && clearInterval(this.heartbeatTimer);
			// 返回APP,延迟2000ms，让用户看到相关提示信息
			setTimeout(() => {
				// 关闭状态
				this.state = "CLOSE";
				// 关闭摄像头等资源
				this.camera.close();
				// 手动推消息到uniapp端进行关闭
				window.uni.postMessage({
					data: {
						key: "WV_CLOSE"
					}
				});
			}, 2000)
			// 停止呼叫语音
			this.$refs.callAudio.pause();
		},
		decodeURL(val) {
			return JSON.parse(decodeURIComponent(val));
		}
	},
	computed: {
		mine() {
			return this.userInfos.find(user => user.id == this.userId);
		},
		inviter() {
			return this.userInfos.find(user => user.id == this.inviterId);
		},
		isChating() {
			return this.state == "CHATING";
		},
		isReady() {
			return this.state == "CHATING" || this.state == "READY";
		},
		isClose() {
			return this.state == "CLOSE";
		},
		isWaiting() {
			return this.state == "WAITING";
		},
		isLeaderMode() {
			return !!this.leaderId;
		},
		videoStyle() {
			return `width:${this.vw}px;height:${this.vh}px;`
		},
		leaderStyle() {
			return `width:${this.leaderVw}px;height:${this.leaderVh}px;`
		},
		userCount() {
			return this.userInfos.length;
		},
		chatTimeString() {
			if (!this.isChating) {
				return "";
			}
			let min = Math.floor(this.chatTime / 60);
			let sec = this.chatTime % 60;
			let strTime = min < 10 ? "0" : "";
			strTime += min;
			strTime += ":";
			strTime += sec < 10 ? "0" : "";
			strTime += sec;
			return strTime;
		}
	},
	watch: {
		userCount: {
			handler(newCount, oldCount) {
				this.refreshVideoWH();
				this.$nextTick(() => {
					this.reLayoutVideo();
				})
			}
		}
	},
	mounted() {
		// 从url中解析带过来的参数
		const url = new URL(window.location.href);
		this.baseUrl = url.searchParams.get("baseUrl")
		this.loginInfo = this.decodeURL(url.searchParams.get("loginInfo"));
		this.inviterId = url.searchParams.get("inviterId");
		this.userId = url.searchParams.get("userId");
		this.isHost = JSON.parse(url.searchParams.get("isHost"));
		this.groupId = url.searchParams.get("groupId")
		this.userInfos = this.decodeURL(url.searchParams.get("userInfos"));
		this.config = this.decodeURL(url.searchParams.get("config"));
		// 待触发 `UniAppJSBridgeReady` 事件后，即可调用 uni 的 API。
		document.addEventListener('UniAppJSBridgeReady', () => {
			// 本事件触发之前不能加载任何图片,否则一旦图片加载失败，会阻塞很长时间
			this.isJSBridgeReady = true;
			// 初始化工作
			this.init();
			// 初始化来自uniapp的事件
			this.initEvent();
			// 判断当前环境
			window.uni.getEnv((env) => {
				console.log('当前环境：' + JSON.stringify(env));
				this.env = env;
			});
			// 通知APP，web-view 已就绪
			window.uni.postMessage({
				data: {
					key: "WV_READY"
				}
			});

		});
	}
}
</script>

<style lang="scss">
.chat-video {
	position: fixed;
	display: flex;
	flex-direction: column;
	width: 100%;
	height: 100%;
	background-size: 100% 100%;
	overflow: hidden;
	background-color: #333;

	.call-box {
		flex: 1;

		.inv-avatar {
			display: flex;
			justify-content: space-around;
			padding-top: 150px;

			.inv-name {
				margin-top: 10px;
				text-align: center;
				font-size: 50px;
				color: white;
			}
		}

		.inv-text {
			margin-top: 20px;
			text-align: center;
			font-size: 28px;
			color: #cfcfcf;
		}

		.user-list-text {
			margin-top: 100px;
			text-align: center;
			font-size: 26px;
			color: #cfcfcf;
		}

		.user-list {
			flex: 1;
			display: flex;
			padding: 10px 50px;
			height: 180px;
			flex-wrap: wrap;
			justify-content: center;
		}
	}

	.video-box {
		flex: 1;
		position: relative;

		.video-list-1 {
			display: flex;
			flex-wrap: wrap;
			position: relative;
		}

		.video-list-2 {
			display: flex;
			flex-direction: column;

			.leader {
				display: flex;
				align-items: center;
				justify-content: center;
			}

			.follower {
				display: flex;
				overflow-x: scroll;
				align-items: center;

				div {
					flex-shrink: 0
				}

			}
		}
	}

	.control-bar {
		height: 280px;
		width: 100%;

		.chat-time {
			text-align: center;
			color: white;
			line-height: 100px;
			height: 100px;
		}

		.dev-bar {
			display: flex;
			justify-content: space-around;
			align-items: center;
			padding: 0 40px;

			.icon-box {
				display: flex;
				flex-direction: column;
				align-items: center;


				.icon {
					border-radius: 50%;
					padding: 20px;
					font-size: 60px;
					width: 100px;
					height: 100px;
					text-align: center;
				}

				.icon-front {
					color: black;
					background-color: white;
				}

				.icon-back {
					color: white;
					background-color: black;
				}

				.icon-text {
					color: white;
					font-size: 30px;
					margin-top: 10px;
				}
			}
		}
	}

	.bottom-bar {
		display: flex;
		justify-content: space-around;
		align-items: center;
		height: 240px;
		padding: 0 40px;

		.icon {
			color: white;
			border-radius: 50%;
			padding: 20px;
			font-size: 100px;
			background-color: #1ba609;

			&.red {
				background-color: #e61d1d;
			}
		}
	}
}
</style>