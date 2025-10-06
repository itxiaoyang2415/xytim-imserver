const requestPermissions = (scope, message) => {
	return new Promise((resolve, reject) => {
		window.plus.android.requestPermissions(
			["android.permission." + scope],
			function(resultObj) {
				console.log(resultObj, "resultObj");
				resolve(resultObj);
				
			},
			function(error) {
				reject()
			}
		);
	});
}


// 跳转权限设置
let topMessageView;
const createTopMessage = (title, message) => {
	topMessageView = new window.plus.nativeObj.View("topMessageView", {
		width: "100%",
		height: "100%",
		backgroundColor: "rgba(0,0,0,0.85)",
	});
	topMessageView.drawText(
		title, {
			top: "-50px",
			left: "10%",
			width: "80%",
			height: "80%",
		}, {
			color: "#ffffff",
			size: "22px",
		}
	);
	topMessageView.drawText(
		message, {
			top: "0px",
			left: "10%",
			width: "80%",
			height: "80%",
		}, {
			color: "#ffffff",
			whiteSpace: "normal",
		}
	);
	// 显示消息提示框
	topMessageView.show();
}

const checkAndRequest = async (scope, title, messageTip, settingTip) => {
	if (window.plus && window.plus.os.name !== "iOS") {
		let t =setTimeout(() => createTopMessage(title, messageTip), 300);
		let res = await requestPermissions(scope, settingTip);
		clearTimeout(t);
		topMessageView && topMessageView.close();
		console.log("res:", res);
		if (!res.granted[0]) {
			// 无权限
			return false;
		}
	}
	return true;
}

const storage = async() => {
	const scope = "WRITE_EXTERNAL_STORAGE"
	const title = "访问媒体和文件权限说明";
	const messageTip = "用于用户发送图片、视频、文件或上传头像等场景";
	const settingTip = "访问媒体和文件权限未获得,此权限用于用户发送图片、视频、文件或上传头像等场景,请前往设置中打开";
	return checkAndRequest(scope, title, messageTip, settingTip);
}

const camera = async () => {
	const scope = "CAMERA"
	const title = "相机使用权限说明";
	const messageTip = "用于拍照、录像、视频通话等场景";
	const settingTip = "相机使用权限未获得,此权限用于拍照、录像、视频通话等场景,请前往设置中打开";
	return checkAndRequest(scope, title, messageTip, settingTip);
}


const micro = async() => {
	const scope = "RECORD_AUDIO"
	const title = "麦克风使用权限说明";
	const messageTip = "用于拍摄、录制语音消息、视频或语音通话等场景";
	const settingTip = "麦克风使用权限未获得,此权限用于用于拍摄、录制语音消息、视频或语音通话等场景,请前往设置中打开";
	return checkAndRequest(scope, title, messageTip, settingTip);
}

export default {
	storage,
	camera,
	micro
}