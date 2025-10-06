<template>
	<div class="head-image">
		<img class="avatar-image" v-if="isReady&&url" :src="url" />
		<div class="avatar-text" v-else :style="avatarTextStyle">
			{{name.substring(0,1).toUpperCase()}}
		</div>
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
		id: {
			type: Number
		},
		size: {
			type: Number,
			default: 50
		},
		url: {
			type: String
		},
		name: {
			type: String,
			default: "?"
		},
		isReady: {
			type: Boolean,
			default: true
		}
	},
	methods: {},
	computed: {
		avatarTextStyle() {
			return `color:${this.textColor};`
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
		width: 300px;
		height: 300px;
		overflow: hidden;
		border-radius: 10%;
	}

	.avatar-text {
		background-color: #f2f2f2;
		/* 默认背景色 */
		border-radius: 50%;
		/* 圆角效果 */
		display: flex;
		align-items: center;
		justify-content: center;
		border: 1px solid #ccc;
		width: 250px;
		height: 250px;
		font-size: 150px;
	}
}
</style>