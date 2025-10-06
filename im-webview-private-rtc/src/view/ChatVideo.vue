<template>
	<div class="chat-video">
		<div v-if="!isJSBridgeReady" class="mask"></div>
		<div v-else class="mask" :style="{ backgroundImage: 'url(' + friend.headImage + ')' }"></div>
		<audio ref="callAudio" loop="true">
			<source src="@/assets/audio/call.wav">
		</audio>
		<audio ref="handupAudio" x5-playsinline playsinline webkit-playsinline>
			<source src="@/assets/audio/handup.wav">
		</audio>
		<div class="friend-avatar" v-show="!isConnected||isVoiceMode">
			<head-image :isReady="isJSBridgeReady" :url="friend.headImage" :name="friend.showNickName" :size="200">
				<div class="friend-name">{{friend.showNickName}}</div>
			</head-image>
		</div>
		<div class="video-box" v-show="isConnected&&isVideoMode">
			<div class="video-friend">
				<video class="video-friend-video" ref="friendVideo" id="friendVideo" x5-video-player-fullscreen="true"
					x5-playsinline playsinline webkit-playsinline>
				</video>
			</div>
			<div class="video-mine">
				<video class="video-mine-video" :class="isFacing?'reverse':''" id="mineVideo" ref="mineVideo" muted
					x5-video-player-fullscreen="true" x5-playsinline playsinline webkit-playsinline></video>
			</div>
		</div>
		<div class="tip" v-show="tip">
			<span class="tip-text">{{tip}}</span>
		</div>
		<div v-show="isConnected" class="chat-time">
			<span class="chat-time-text">{{chatTimeString}}</span>
		</div>
		<div class="control-bar">
			<div v-show="isConnected && isMicroPhone" class="icon iconfont icon-microphone-on icon-tool"
				@click="onSwitchMicroPhone()"></div>
			<div v-show="isConnected && !isMicroPhone" class="icon iconfont icon-microphone-off icon-tool"
				@click="onSwitchMicroPhone()"></div>
			<div v-show="!isHost && !isConnected" class="icon iconfont icon-phone-reject icon-rtc red"
				@click="onReject()"></div>
			<div v-show="!isHost && !isConnected" class="icon iconfont icon-phone-accept icon-rtc" @click="onAccept()">
			</div>
			<div v-show="isHost && !isConnected" class="icon iconfont icon-phone-reject icon-rtc red"
				@click="onCancel()">
			</div>
			<div v-show="isConnected" class="icon iconfont icon-phone-reject icon-150 icon-rtc red" @click="onHandup()">
			</div>
			<div v-show="isConnected && isVideoMode && isFacing" class="icon iconfont icon-camera-front icon-tool"
				@click="onSwitchCamera()"></div>
			<div v-show="isConnected && isVideoMode && !isFacing" class="icon iconfont icon-camera-back icon-tool"
				@click="onSwitchCamera()"></div>
			<div v-show="isConnected && isVoiceMode && isSpeaker" class="icon iconfont icon-speaker-on icon-tool"
				@click="onSwitchSpeaker()"></div>
			<div v-show="isConnected && isVoiceMode && !isSpeaker" class="icon iconfont icon-speaker-off icon-tool"
				@click="onSwitchSpeaker()"></div>
		</div>
	</div>
</template>

<script>
import ImWebRtc from '@/common/webrtc'
import ImCamera from '@/common/camera'
import ImApi from '@/common/api'
import UniEvent from '@/common/uniEvent'
import HeadImage from '@/components/HeadImage'

export default {
	data() {
		return {
			env: {},
			isJSBridgeReady: false,
			mode: "video", // video: 视频聊天,voice :语音聊天
			camera: new ImCamera(), // 摄像头和麦克风
			webrtc: new ImWebRtc(), // webrtc相关
			uniEvent: new UniEvent(), // 接受来自uniapp的消息
			API: null, // API接口请求对象
			localStream: null, // 本地视频
			remoteStream: null, // 对方视频
			isHost: true, // 是否呼叫方
			isMicroPhone: true, // 是否开启麦克风
			isFacing: true, // 是否前置摄像头
			isSpeaker: true, // 是否开启扬声器
			isLock: false, // 防抖锁定标志，如果重复点击接听会报错
			tip: "", // 提示文字
			state: "INIT", // INIT:初始状态  WAITING:等待呼叫或接听 CHATING:聊天中  CLOSE:关闭 ERROR:出现异常
			candidates: [], // 候选信息
			chatTime: 0, // 聊天时长
			audioPlayer: null, // app的音频播放器
			disconnectTimer: null, // 断开连接超时退出定时器
			chatTimer: null, // 聊天计时器
			heartbeatTimer: null, // 心跳定时器
			baseUrl: "", // 服务器基础接口路径
			loginInfo: {}, // token信息
			userId: null, // 当前用户id
			friend: {}, // 好友信息
			config: {} // 系统配置信息
		};
	},
	components: {
		HeadImage
	},
	methods: {
		onSwitchMicroPhone() {
			this.isMicroPhone = !this.isMicroPhone;
			if (this.localStream) {
				this.localStream.getTracks().forEach((track => {
					if (track.kind === 'audio') {
						track.enabled = this.isMicroPhone
					}
				}))
			}
			console.log("麦克风:" + this.isMicroPhone)
		},
		onSwitchCamera() {
			this.isFacing = !this.isFacing;
			this.camera.openVideo(this.isFacing).then((localStream) => {
				this.localStream = localStream;
				// 如果麦克风已经关闭，需要禁用声音频道
				if (!this.isMicroPhone) {
					this.localStream.getTracks().forEach((track => {
						if (track.kind === 'audio') {
							track.enabled = false;
						}
					}))
				}
				// 切换传输的视频流
				this.webrtc.switchStream(localStream);
				let cameraName = this.isFacing ? "前置" : "后置"
				// 显示本地视频
				this.$refs.mineVideo.srcObject = localStream;
				this.$refs.mineVideo.play().catch(() => {
					console.log("播放本地视频失败")
				});
				console.log("摄像头切换:" + cameraName);
			}).catch((e) => {
				console.log("摄像头切换失败:" + e.message)
			})
		},
		onSwitchSpeaker() {
			this.isSpeaker = !this.isSpeaker;
			const video = document.getElementById("friendVideo");
			video.pause();
			video.muted = !this.isSpeaker;
			video.play();
			// if(window.plus){
			// 	if(!this.audioPlayer){
			// 		this.audioPlayer = window.plus.audio.createPlayer();
			// 	}
			// 	// 扬声器
			// 	let route=window.plus.audio.ROUTE_SPEAKER;
			// 	if(!this.isSpeaker){
			// 		// 听筒
			// 		route = window.plus.audio.ROUTE_EARPIECE
			// 	}
			// 	this.audioPlayer.setRoute(route);
			// }
			console.log("扬声器切换:" + this.isSpeaker);
		},
		onCall() {
			// 发起呼叫
			this.API.setup(this.friend.id, this.mode).then(() => {
				this.tip = "等待对方接受邀请...";
				this.state = "WAITING";
				this.$refs.callAudio.play().catch((e) => {
					console.log("播放呼叫语音失败")
				});
			}).catch((e) => {
				this.close(e.message);
			});
		},
		onAccept() {
			// 用锁实现防抖，如果用户手快多次点击接听会报错
			if (!this.tryLock()) {
				console.log("accept防抖触发")
				return false;
			}
			// 停止呼叫铃声
			this.$refs.callAudio.pause();
			// 检查设备
			if (!this.checkDevEnable()) {
				this.API.failed(this.friend.id, "对方设备不支持通话");
				return false;
			}
			// 初始化webrtc
			this.initRtc(() => {
				// 接受对方的邀请
				this.API.accept(this.friend.id).catch((e) => {
					this.tip = e.message;
				});
			})
		},
		onReject() {
			if (!this.tryLock()) {
				console.log("reject防抖触发")
				return false;
			}
			// 拒绝通话
			this.API.reject(this.friend.id);
			this.close("您已拒绝通话");
		},
		onCancel() {
			if (!this.tryLock()) {
				console.log("cancel防抖触发")
				return false;
			}
			// 取消呼叫
			this.API.cancel(this.friend.id);
			this.close("您已取消呼叫");
		},
		onHandup() {
			// 挂断
			this.API.handup(this.friend.id);
			this.close("您已挂断,通话结束");
		},
		onConnected() {
			console.log("webrtc连接成功")
			this.tip = "";
			this.disconnectTimer && clearTimeout(this.disconnectTimer)
		},
		onDisconnected() {
			console.log("webrtc网络断开")
			this.tip = "当前通话质量不佳";
			// 15s秒内没有重连回来，就中断视频
			this.disconnectTimer = setTimeout(() => {
				this.close("网络异常，通话结束");
			}, 15000);
		},
		onNavBack() {
			console.log("强制退出");
			if (this.isHost && this.state == "WAITING") {
				// 强制终止呼叫
				console.log("强制终止呼叫");
				this.API.cancel(this.friend.id);
			} else if (!this.isHost && this.state == "WAITING") {
				// 强制拒绝接听
				console.log("强制拒绝接听");
				this.API.reject(this.friend.id);
			} else if (this.state == "CHATING") {
				// 强制退出聊天
				this.API.handup(this.friend.id);
			}
		},
		onRTCSetup(msg) {
			// 收到对方的呼叫
			this.state = "WAITING";
			this.tip = `邀请您${this.isVideoMode? "视频":"语音"}通话...`;
			this.$refs.callAudio.play().catch((e) => {
				console.log("播放呼叫语音失败")
			});
		},
		onRTCAccept(msg) {
			// 收到对方接受通话消息
			if (msg.selfSend) {
				// 我在其他终端接受了对方的通话
				this.close("已在其他设备接听");
			} else {
				// 对方接受了通话
				this.webrtc.createOffer().then((offer) => {
					this.$refs.callAudio.pause();
					// 推送offer给对方
					this.API.offer(this.friend.id, offer);
				});
			}
		},
		onRTCRejct(msg) {
			if (msg.selfSend) {
				// 我在其他终端拒绝了对方的通话
				this.close("已在其他设备拒绝通话");
			} else {
				// 对方拒绝了我的通话
				this.close("对方拒绝了您的通话请求");
			}
		},
		onRTCCancel(msg) {
			this.close("对方取消了呼叫");
		},
		onRTCFailed(msg) {
			if (msg.selfSend) {
				this.close("您未接听");
			} else {
				this.close(msg.content);
			}

		},
		onRTCHandup(msg) {
			this.close("对方已挂断,通话结束", "对方已挂断");
		},
		onRTCOffer(msg) {
			const offer = JSON.parse(msg.content);
			this.webrtc.createAnswer(offer).then((answer) => {
				// 推送answer给对方
				this.API.answer(this.friend.id, answer);
			})
			// 状态为聊天中
			this.state = 'CHATING';
			// 记录时长
			this.startChatTime();
		},
		onRTCAnswer(msg) {
			const answer = JSON.parse(msg.content);
			this.webrtc.setRemoteDescription(answer);
			// 进入聊天状态
			this.state = "CHATING";
			// 推送候选人信息
			this.sendCandidate();
			// 开始计时
			this.startChatTime();
		},
		onRTCCandidate(msg) {
			this.webrtc.addIceCandidate(JSON.parse(msg.content));
		},
		onRTCMessage(msg) {
			// 防止被其他人消息干扰
			if (msg.sendId != this.friend.id &&
				msg.sendId != this.userId &&
				msg.type != this.$enums.MESSAGE_TYPE.RTC_SETUP_VIDEO &&
				msg.type != this.$enums.MESSAGE_TYPE.RTC_SETUP_VOICE) {
				return;
			}
			// RTC信令处理
			switch (msg.type) {
				case this.$enums.MESSAGE_TYPE.RTC_SETUP_VIDEO:
					this.onRTCSetup(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_SETUP_VOICE:
					this.onRTCSetup(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_ACCEPT:
					this.onRTCAccept(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_REJECT:
					this.onRTCRejct(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_CANCEL:
					this.onRTCCancel(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_HANDUP:
					this.onRTCHandup(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_FAILED:
					this.onRTCFailed(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_OFFER:
					this.onRTCOffer(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_ANSWER:
					this.onRTCAnswer(msg)
					break;
				case this.$enums.MESSAGE_TYPE.RTC_CANDIDATE:
					this.onRTCCandidate(msg)
					break;
			}
		},
		tryLock() {
			if (this.isLock) {
				return false;
			}
			this.isLock = true;
			// 2s后解锁
			setTimeout(() => {
				this.isLock = false;
			}, 2000)
			return true;
		},
		startChatTime() {
			this.chatTimer && clearInterval(this.chatTimer);
			this.chatTimer = setInterval(() => {
				this.chatTime++;
			}, 1000)
		},
		startHeartBeat() {
			// 每15s推送一次心跳
			this.heartbeatTimer && clearInterval(this.heartbeatTimer);
			this.heartbeatTimer = setInterval(() => {
				this.API.heartbeat(this.friend.id);
			}, 15000)
		},
		sendCandidate() {
			this.candidates.forEach((candidate) => {
				this.API.sendCandidate(this.friend.id, candidate);
			});
			this.candidates = [];
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
		connected(localStream) {
			console.log("webrtc开始连接")
			this.localStream = localStream;
			// 显示本地视频
			this.$refs.mineVideo.srcObject = localStream;
			document.getElementById('mineVideo').muted = true;
			this.$refs.mineVideo.play().catch(() => {
				console.log("本地流播放异常")
			});
			// 建立webrtc连接
			this.webrtc.setupPeerConnection(localStream, (remoteStream) => {
				// 对方视频流
				console.log("获取到远端流")
				this.remoteStream = remoteStream;
				this.$refs.friendVideo.srcObject = remoteStream;
				this.$refs.friendVideo.play().catch(() => {
					console.log("远端流播放异常")
				});
			})
			// 监听候选信息
			this.webrtc.onIcecandidate((candidate) => {
				if (this.state == "CHATING") {
					// 连接已就绪,直接发送
					this.API.sendCandidate(this.friend.id, candidate);
				} else {
					// 连接未就绪,缓存起来，连接后再发送
					this.candidates.push(candidate)
				}
			})
			// 监听连接成功状态
			this.webrtc.onStateChange((state) => {
				if (state == "connected") {
					this.onConnected();
				} else if (state == "disconnected") {
					this.onDisconnected();
				}
			})
		},
		refreshTitle() {
			let strTitle = this.mode == "video" ? "视频通话" : "语音通话";
			if (this.friend.showNickName) {
				strTitle += "-" + this.friend.showNickName
			}
			document.title = strTitle;
		},
		checkDevEnable() {
			// 检测摄像头
			if (!this.camera.isEnable()) {
				console.log("未检测到摄像头...")
				this.state = "ERROR";
				this.tip = "未检测到摄像头";
				return false;
			}
			// 检测webrtc
			if (!this.webrtc.isEnable()) {
				console.log("初始化RTC失败...")
				this.state = "ERROR";
				this.tip = "初始化RTC失败，原因可能是: 1.服务器缺少ssl证书 2.您的设备不支持WebRTC";
				return false;
			}
			return true;
		},
		initRtc(callback) {
			console.log("初始化webrtc...")
			// 初始化webrtc
			const configuration = {
				iceServers: this.config.iceServers
			};
			this.webrtc.init(configuration);
			if (this.mode == "video") {
				// 打开摄像头
				this.camera.openVideo(this.isFacing).then((localStream) => {
					console.log("流打开成功")
					this.connected(localStream);
					callback();
				}).catch((e) => {
					console.log("流打开失败:" + e.message)
					this.tip = e.message;
					// 失败也进行连接，主要为了方便本地调试
					this.connected();
					callback();
				})
			} else {
				// 打开麦克风
				this.camera.openAudio().then((localStream) => {
					console.log("流打开成功")
					this.connected(localStream);
					callback();
				}).catch((e) => {
					console.log("流打开失败:" + e.message)
					this.tip = e.message;
					// 失败也进行连接，主要为了方便本地调试
					this.connected();
					callback();
				})
			}
		},
		close(tip) {
			this.tip = tip;
			// 停止呼叫语音
			this.$refs.callAudio.pause();
			// 播放挂断语音
			this.$refs.handupAudio.play().catch((e) => {
				console.log("播放挂断语音失败")
			});
			// 清理定时器
			this.chatTimer && clearInterval(this.chatTimer);
			this.heartbeatTimer && clearInterval(this.heartbeatTimer);
			// 返回APP,延迟1500ms，让用户看到相关提示信息
			setTimeout(() => {
				// 关闭状态
				this.state = "CLOSE";
				// 关闭摄像头等资源
				this.camera.close();
				this.webrtc.close();
				this.$refs.mineVideo.srcObject = null;
				this.$refs.friendVideo.srcObject = null;
				// 手动推消息到uniapp端进行关闭
				window.uni.postMessage({
					data: {
						key: "WV_CLOSE"
					}
				});
			}, 1500)
		},
		decodeURL(val) {
			return JSON.parse(decodeURIComponent(val));
		}
	},
	computed: {
		isConnected() {
			return this.state == "CHATING";
		},
		isVideoMode() {
			return this.mode == "video";
		},
		isVoiceMode() {
			return this.mode == "voice";
		},
		chatTimeString() {
			let min = Math.floor(this.chatTime / 60);
			let sec = this.chatTime % 60;
			let strTime = min < 10 ? "0" : "";
			strTime += min;
			strTime += ":"
			strTime += sec < 10 ? "0" : "";
			strTime += sec;
			return strTime;
		}
	},
	mounted() {
		// 模式：视频通话/语音通话
		const url = new URL(window.location.href);
		this.mode = url.searchParams.get("mode");
		this.isHost = JSON.parse(url.searchParams.get("isHost"))
		this.baseUrl = url.searchParams.get("baseUrl")
		this.userId = url.searchParams.get("userId")
		this.loginInfo = this.decodeURL(url.searchParams.get("loginInfo"));
		this.friend = this.decodeURL(url.searchParams.get("friend"));
		this.config = this.decodeURL(url.searchParams.get("config"));
		// 刷新标题
		this.refreshTitle();
		// 创建API对象
		this.API = new ImApi(this.baseUrl, this.loginInfo);
		// 初始化来自uniapp的事件
		this.initEvent();
		// 开启心跳定时
		this.startHeartBeat();
		// 待触发 `UniAppJSBridgeReady` 事件后，即可调用 uni 的 API。
		document.addEventListener('UniAppJSBridgeReady', () => {
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
			// 本事件触发之前不能加载任何图片,否则一旦图片加载失败，会阻塞很长时间
			this.isJSBridgeReady = true;
			// 如果是呼叫方，直接开始初始化RTC
			if (this.isHost && this.checkDevEnable()) {
				this.initRtc(() => {
					// 呼叫方必须成功打开本地流才能正常呼叫
					if (this.localStream) {
						this.onCall();
					}
				});
			}
		});
	}
}
</script>

<style lang="scss" scoped>
.mask {
	position: absolute;
	width: 100%;
	height: 100%;
	filter: blur(50px);
	z-index: 0;
}

.chat-video {
	position: fixed;
	width: 100%;
	height: 100%;
	background-size: 100% 100%;
	overflow: hidden;
	background: linear-gradient(145deg, #ffffff20 25%, #00000060), #333;


	.friend-avatar {
		display: flex;
		justify-content: space-around;
		padding-top: 200px;

		.friend-name {
			margin-top: 5px;
			text-align: center;
			font-size: 40px;
			color: black;
		}
	}

	.video-box {
		position: relative;
		height: 100%;

		.video-mine {
			position: fixed;
			z-index: 9999;
			top: 20px;
			right: 20px;
			background-color: grey;

			.video-mine-video {
				width: 200px;
				min-height: 200px;
				max-height: 500px;
				object-fit: cover;
				display: block;
			}

			.reverse {
				// 本地画面翻转: 开启后部分IOS手机会出现黑屏，暂时屏蔽
				// transform: rotateY(180deg);
			}
		}

		.video-friend {
			position: relative;
			width: 100%;
			height: 100%;
			background-color: darkgray;

			.video-friend-video {
				width: 100%;
				height: 100%;
				object-fit: cover;
			}
		}
	}

	.tip {
		position: fixed;
		bottom: 360px;
		width: 100%;
		text-align: center;

		.tip-text {
			font-size: 30px;
			padding: 12px;
			color: white;
			background: #303030a0;
			border-radius: 10px;
		}
	}

	.chat-time {
		position: fixed;
		bottom: 260px;
		width: 100%;
		text-align: center;

		.chat-time-text {
			font-size: 35px;
			padding: 10px;
			color: white;
			background: #303030a0;
			border-radius: 10px;
		}
	}

	.control-bar {
		position: fixed;
		bottom: 80px;
		display: flex;
		justify-content: space-around;
		align-items: center;
		margin-top: 20px;
		width: 100%;

		.icon-rtc {
			color: white;
			border-radius: 50%;
			padding: 20px;
			font-size: 100px;
			background-color: #1ba609;

			&.red {
				background-color: #e61d1d;
			}
		}

		.icon-tool {
			color: white;
			border-radius: 50%;
			padding: 20px;
			font-size: 80px;
			background-color: #4f4f4f;
		}
	}
}
</style>