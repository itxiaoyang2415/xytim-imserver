<template>
	<div class="remote-video" ref="box">
		<van-loading v-show="!isConnected" class="loading" color="#5870e6 " size="50" />
		<head-image v-show="!isShowVideo" class="head-image" :width="width" :height="height" :url="userInfo.headImage"
			:name="userInfo.nickName" :radius="'0'" :isReady="isJSBridgeReady"></head-image>
		<div class="info-bar">
			<div v-show="userInfo.isMicroPhone" class="icon iconfont icon-microphone-on"></div>
			<div v-show="!userInfo.isMicroPhone" class="icon iconfont icon-microphone-off"></div>
			<div class="name">
				{{userInfo.nickName}}
			</div>
		</div>
		<video v-show="isShowVideo" id="remoteVideo" class="video" :class="userInfo.isShareScreen?'contain':''"
			ref="video" x5-video-player-fullscreen="true" x5-playsinline playsinline webkit-playsinline>
		</video>
	</div>
</template>

<script>
import ImWebRtc from '@/common/webrtc'
import ImCamera from '@/common/camera'
import HeadImage from '@/components/HeadImage'
export default {
	name: "localVideo",
	components: {
		HeadImage
	},
	data() {
		return {
			API: null,
			webrtc: new ImWebRtc(), // webrtc相关
			localStream: null,
			remoteStream: null,
			candidates: [],
			isInit: false,
			isConnected: false,
			isReady: false
		}
	},
	props: {
		width: {
			type: Number
		},
		height: {
			type: Number
		},
		groupId: {
			type: String
		},
		userInfo: {
			type: Object
		},
		isJSBridgeReady: {
			type: Boolean,
			default: true
		}
	},
	methods: {
		init(API, configuration, localStream) {
			this.isInit = true;
			this.API = API;
			this.webrtc.init(configuration);
			this.localStream = localStream;
			// 建立webrtc连接
			this.webrtc.setupPeerConnection((remoteStream) => {
				// 对方视频流
				console.log("获取到远端流")
				this.remoteStream = remoteStream;
				this.$refs.video.srcObject = remoteStream;
				this.$refs.video.play().catch(() => {
					console.log("远端流播放失败")
				});
			})
			// 设置本地流
			this.webrtc.setStream(localStream);
			// 监听候选信息
			this.webrtc.onIcecandidate((candidate) => {
				if (this.isReady) {
					// 连接已就绪,直接发送
					this.API.candidate(this.groupId, this.userInfo.id, JSON.stringify(candidate));
				} else {
					// 连接未就绪,缓存起来，连接后再发送
					this.candidates.push(candidate)
				}
			})
			// 监听连接成功状态
			this.webrtc.onStateChange((state) => {
				if (state == "connected") {
					// 就绪
					this.isConnected = true;
				} else if (state == "disconnected") {
					//this.$toast("当前通话质量不佳")
				}
			})
		},
		reconnect(localStream) {
			this.localStream = localStream;
			this.webrtc.setStream(localStream);
			this.connect();
		},
		switchStream(localStream) {
			this.localStream = localStream;
			this.webrtc.switchStream(localStream);
		},
		setMute(isMute) {
			const video = document.getElementById("remoteVideo");
			video.pause();
			video.muted = isMute;
			video.play();
		},
		connect() {
			this.webrtc.createOffer().then((offer) => {
				// 推送offer给对方
				this.API.offer(this.groupId, this.userInfo.id, JSON.stringify(offer));
			});
		},
		onOffer(offer) {
			this.webrtc.createAnswer(offer).then((answer) => {
				// 推送answer给对方
				this.API.answer(this.groupId, this.userInfo.id, JSON.stringify(answer));
			});
			this.isReady = true;
		},
		onAnswer(answer) {
			// 设置对方answer信息
			this.webrtc.setRemoteDescription(answer);
			// 推送candidate
			this.sendCandidate();
			this.isReady = true;
		},
		setCandidate(candidate) {
			this.webrtc.addIceCandidate(candidate);
		},
		sendCandidate() {
			this.candidates.forEach((candidate) => {
				this.API.candidate(this.groupId, this.userInfo.id, JSON.stringify(candidate));
			});
			this.candidates = [];
		},
		close() {
			this.webrtc.close();
			this.$refs.video.srcObject = null;
			this.isInit = false;
			this.isConnected = false;
			this.isReady = false;
			this.candidates = [];
		}
	},
	computed: {
		isShowVideo() {
			return this.isConnected && (this.userInfo.isCamera ||
				this.userInfo.isShareScreen)
		}
	}
}
</script>

<style lang="scss" scoped>
.remote-video {
	position: relative;
	width: 100%;
	height: 100%;
	background: darkgray;

	.loading {
		position: absolute;
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: space-around;
		align-items: center;
		z-index: 99
	}

	.head-image {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		object-fit: cover;
		z-index: 1;
	}

	.info-bar {
		position: absolute;
		bottom: 10px;
		left: 10px;
		text-align: left;
		overflow: hidden;
		color: white;
		font-size: 24px;
		display: flex;
		z-index: 2;
		background-color: #888;
		padding: 6px;
		border-radius: 10px;

		.icon {
			font-size: 30px;
		}
	}

	.video {
		position: relative;
		width: 100%;
		height: 100%;
		object-fit: cover;
		background: darkgray;

		&.contain {
			object-fit: contain;
		}

	}
}
</style>