import permission from "./permission"

class ImCamera {
	constructor() {
		this.stream = null;
	}
}

ImCamera.prototype.isEnable = function() {
	return !!navigator && !!navigator.mediaDevices && !!navigator.mediaDevices.getUserMedia;
}

ImCamera.prototype.openVideo = async function(isFacing) {
	if (this.stream) {
		this.close();
	}
	return new Promise((resolve, reject) => {
		setTimeout(async () => {
			if (!await permission.camera()) {
				return reject({
					message: "未能获取摄像头访问权限"
				})
			}
			if (!await permission.micro()) {
				return reject({
					message: "未能获取麦克风权限"
				})
			}
			let ratio = window.devicePixelRatio || 1;	
			let facingMode = isFacing ? "user" : "environment";
			let constraints = {
				video: {
					// 对于webrtc,屏幕长宽是反过来的
					width: window.screen.height * ratio,
					height: window.screen.width * ratio,
					facingMode: facingMode
				},
				audio: {
					echoCancellation: true, //音频开启回音消除
					noiseSuppression: true // 开启降噪
				}
			}
			navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
				console.log("摄像头打开")
				this.stream = stream;
				resolve(stream);
			}).catch((e) => {
				console.log("摄像头未能正常打开", e)
				reject({
					code: 0,
					message: "摄像头未能正常打开"
				})
			})
		})
	})
}

ImCamera.prototype.openAudio = function() {
	if (this.stream) {
		this.close();
	}
	return new Promise((resolve, reject) => {
		setTimeout(async () => {
			if (!await permission.micro()) {
				return reject({
					code: 0,
					message: "未能获取麦克风权限"
				})
			}
			let constraints = {
				video: false,
				audio: {
					echoCancellation: true, //音频开启回音消除
					noiseSuppression: true // 开启降噪
				}
			}
			navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
				this.stream = stream;
				resolve(stream);
			}).catch(() => {
				console.log("麦克风未能正常打开")
				reject({
					code: 0,
					message: "麦克风未能正常打开"
				})
			})
		})
	})
}

ImCamera.prototype.close = function() {
	// 停止流
	if (this.stream) {
		this.stream.getTracks().forEach((track) => {
			track.stop();
		});
		this.stream = null;
	}
}

export default ImCamera;