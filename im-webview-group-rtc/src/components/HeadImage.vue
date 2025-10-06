<template>
	<div class="head-image">
		<van-image class="avatar-image" v-if="isReady&&url" :src="url" :style="avatarImageStyle" loading="lazy" />
		<div class="avatar-text" v-else :style="avatarTextStyle">
			{{name.substring(0,1).toUpperCase()}}
		</div>
		<div v-if="online" class="online" title="用户当前在线"></div>
		<slot></slot>
	</div>
</template>

<script>
export default {
	name: "headImage",
	data() {
		return {
			colors: ["#7dd24b", "#c7515a", "#db68ef", "#15d29b", "#85029b",
				"#c9b455", "#fb2609", "#bda818", "#af0831", "#326eb6"
			]
		}
	},
	props: {
		size: {
			type: Number,
			default: 50
		},
		width: {
			type: Number
		},
		radius: {
			type: String,
			default: "10%"
		},
		height: {
			type: Number
		},
		url: {
			type: String
		},
		name: {
			type: String,
			default: "?"
		},
		online: {
			type: Boolean,
			default: false
		},
		isReady: {
			type: Boolean,
			default: true
		}
	},
	methods: {},
	computed: {
		avatarImageStyle() {
			let w = this.width ? this.width : this.size;
			let h = this.height ? this.height : this.size;
			return `width:${w}px; height:${h}px;
					border-radius: ${this.radius};`
		},
		avatarTextStyle() {
			let w = this.width ? this.width : this.size;
			let h = this.height ? this.height : this.size;
			return `width: ${w}px;height:${h}px;
					color:${this.textColor};font-size:${w*0.6}px;
					border-radius: ${this.radius};`
		},
		textColor() {
			let hash = 0;
			for (var i = 0; i < this.name.length; i++) {
				hash += this.name.charCodeAt(i);
			}
			return this.colors[hash % this.colors.length];
		}
	}
}
</script>

<style scoped lang="scss">
.head-image {
	position: relative;
	cursor: pointer;

	.avatar-image {
		position: relative;
		overflow: hidden;
		display: block;
	}

	.avatar-text {
		background-color: #f2f2f2;
		display: flex;
		align-items: center;
		justify-content: center;
		border: 1px solid #ccc;
	}

	.online {
		position: absolute;
		right: -10%;
		bottom: 0;
		width: 28px;
		height: 28px;
		background: limegreen;
		border-radius: 50%;
		border: 4px solid white;
	}
}
</style>