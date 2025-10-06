<template>
	<div class="local-video">
		<head-image v-show="!isShowVideo" class="head-image" :width="width" :height="height"
			:url="userInfo.headImage" :name="userInfo.nickName" :radius="'0'" :isReady="isJSBridgeReady"></head-image>
		<div v-show="isShowVideo" class="control-bar">
			<div v-show="isReady && isFacing" class="icon iconfont icon-camera-front" @click.stop="onSwitchFacing()">
			</div>
			<div v-show="isReady && !isFacing" class="icon iconfont icon-camera-back" @click.stop="onSwitchFacing()">
			</div>
		</div>
		<video v-show="isShowVideo" class="video" id="localVideo" ref="video"
			x5-video-player-fullscreen="true" x5-playsinline playsinline webkit-playsinline muted></video>
	</div>
</template>

<script>
import ImCamera from '@/common/camera'
import HeadImage from '@/components/HeadImage'
export default {
	name: "localVideo",
	components: {
		HeadImage
	},
	data() {
		return {
			camera: new ImCamera(), // 摄像头和麦克风
			stream: null,
			isReady: false,
			isFacing: true
		}
	},
	props: {
		userInfo: {
			type: Object
		},
		width: {
			type: Number
		},
		height: {
			type: Number
		},
		isJSBridgeReady: {
			type: Boolean,
			default: true
		}
	},
	methods: {
		open(stream) {
			this.stream = stream;
			// 显示本地视频
			this.$refs.video.srcObject = stream;
			// 这种方式有部分设备会失效
			// this.$refs.video.muted = true;
			// 这种原始方式才好使
			document.getElementById("localVideo").muted = true;
			this.$refs.video.play().catch(() => {
				console.log("本地流播放异常")
			});
			this.isReady = !!stream;
		},
		setMicroPhone(isEnable) {
			this.stream.getTracks().forEach((track => {
				if (track.kind === 'audio') {
					track.enabled = isEnable;
				}
			}))
		},
		setCamera(isEnable) {
			this.stream.getTracks().forEach((track => {
				if (track.kind === 'video') {
					track.enabled = isEnable;
				}
			}))
		},
		onSwitchFacing() {
			this.isFacing = !this.isFacing;
			this.$emit("switchFacing", this.isFacing);
		}
	},
	computed: {
		isShowVideo() {
			return this.isReady && (this.userInfo.isCamera 
				|| this.userInfo.isShareScreen)
		}
	}
}
</script>

<style lang="scss" scoped>
.local-video {
	position: relative;
	width: 100%;
	height: 100%;
	background-color: darkgray;

	.head-image {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		z-index: 1;
	}

	.video {
		position: relative;
		width: 100%;
		height: 100%;
		object-fit: cover;
		display: block;
	}

	.control-bar {
		position: absolute;
		width: 100%;
		height: 80px;
		bottom: 0;
		display: flex;
		justify-content: flex-end;
		padding: 10px;
		z-index: 2;

		.icon {
			border-radius: 50%;
			padding: 10px;
			font-size: 40px;
			color: white;
			background-color: black;
			opacity: 0.8;
		}
	}
}
</style>