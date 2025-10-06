<template>
	<div class="invite-member">
		<div class="invite-btn">
			<div class="icon iconfont icon-invite-rtc" @click="onPopup()"></div>
			<div class="inv-text">点击邀请</div>
		</div>
		<van-popup v-model="isPopup" position="bottom">
			<div class="popup-content">
				<div class="top-bar">
					<div class="top-tip">邀请成员加入通话</div>
					<van-button class="top-btn" type="danger" size="middle" @click="onClean()">清空 </van-button>
					<van-button class="top-btn" type="info" size="middle"
						@click="onOk()">确定({{checkedSize}})</van-button>
				</div>
				<div v-show="checkedSize>0">
					<div class="checked-users">
						<div v-for="m in members" v-show="m.checked" class="user-item" :key="m.id">
							<head-image :name="m.showNickName" :url="m.headImage" :size="30"
								:isReady="isJSBridgeReady"></head-image>
						</div>
					</div>
				</div>
				<div class="search-bar">
					<van-search v-model="searchText" :clearable="false" :show-action="false" placeholder="搜索" />
				</div>
				<div class="member-items">
					<div class="scroll-bar" scroll="true">
						<div v-for="m in members" v-show="m.showNickName.includes(searchText)" :key="m.userId">
							<div class="member-item" @click="onClickItem(m)">
								<head-image :name="m.showNickName" :online="m.online" :url="m.headImage" :size="50"
									:isReady="isJSBridgeReady"></head-image>
								<div class="member-name">{{ m.showNickName}}</div>
								<div class="member-checked">
									<van-checkbox v-model="m.checked" @change=onSwitchChecked(m) :disabled="m.locked"
										checked-color="#0283ef">
									</van-checkbox>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</van-popup>
	</div>
</template>

<script>
import HeadImage from '@/components/HeadImage'

export default {
	name: "InviteMember",
	components: {
		HeadImage
	},
	data() {
		return {
			isPopup: false,
			searchText: "",
			members: []
		}
	},
	props: {
		API: {
			type: Object
		},
		groupId: {
			type: String
		},
		userInfos: {
			type: Array
		},
		maxChannel: {
			type: Number
		},
		isJSBridgeReady: {
			type: Boolean,
			default: true
		}
	},
	methods: {
		onPopup() {
			this.isPopup = true;
			// 查找群用户
			this.API.findGroupMembers(this.groupId).then((members) => {
				members.forEach((m) => {
					m.checked = this.isExist(m.userId);
					m.locked = m.checked;
				});
				this.members = members.filter(m => !m.quit);
			})
		},
		onSwitchChecked(m) {
			if (this.checkedSize > this.maxChannel) {
				m.checked = false;
				this.$toast(`最多支持${this.maxChannel}人同时语音通话`);
			}
		},
		onClickItem(m) {
			if (!m.locked) {
				m.checked = !m.checked;
				this.onSwitchChecked(m)
			}
		},
		onClean() {
			this.members.forEach((m) => {
				if (!m.locked) {
					m.checked = false;
				}
			})
		},
		onOk() {
			// 发起邀请
			let userInfos = [];
			this.members.forEach(m => {
				if (m.checked && !m.locked) {
					userInfos.push({
						id: m.userId,
						nickName: m.showNickName,
						headImage: m.headImage,
						isCamera: false,
						isMicroPhone: true,
						isShareScreen: false
					})
				}
			})
			if (userInfos.length > 0) {
				this.API.invite(this.groupId, userInfos);
				// 显示邀请的用户
				this.$emit("ok", userInfos);
			}
			// 关闭
			this.isPopup = false;
		},
		isExist(userId) {
			return !!this.userInfos.find(user => user.id == userId);
		}
	},
	computed: {
		checkedSize() {
			return this.members.filter(m => m.checked).length
		}
	}
}
</script>

<style lang="scss">
.van-popup {
	border-radius: 20px 20px 0 0 !important;
	overflow: hidden;

	.van-search {
		padding: 6px 6px !important;

		.van-search__content {
			display: block !important;

			.van-field__left-icon {
				line-height: 50px !important;
			}

			.van-field__control {
				text-align: left !important;
				line-height: 50px !important;
			}
		}
	}
}

.invite-member {
	position: relative;
	width: 100%;
	height: 100%;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	color: white;
	background-color: black;

	.invite-btn {
		display: flex;
		flex-direction: column;

		.icon {
			text-align: center;
			font-size: 80px;
		}

		.inv-text {
			font-size: 30px;
			text-align: center;
			margin-top: 10px;
		}
	}
}

.popup-content {
	position: relative;
	border: #dddddd solid 1px;
	display: flex;
	flex-direction: column;
	background-color: white;
	padding: 15px;
	color: black;



	.top-bar {
		display: flex;
		align-items: center;
		height: 100px;
		padding: 10px 20px;

		.top-tip {
			flex: 1;
		}

		.top-btn {
			margin-left: 10px;
		}
	}

	.checked-users {
		display: flex;
		align-items: center;
		height: 90px;
		overflow-x: scroll;
		padding: 0 15px;

		.user-item {
			padding: 3px;
		}
	}

	.member-items {
		position: relative;
		flex: 1;
		overflow: hidden;

		.member-item {
			height: 120px;
			display: flex;
			position: relative;
			align-items: center;
			background-color: white;
			white-space: nowrap;
			padding: 0 15px;

			.member-name {
				flex: 1;
				padding-left: 20px;
				font-size: 30px;
				font-weight: 600;
				line-height: 60px;
				white-space: nowrap;
				overflow: hidden;
				color: black;
			}

			.van-icon {
				border-radius: 50% !important;
			}
		}

		.scroll-bar {
			height: 800px;
			overflow-y: scroll;

		}
	}
}
</style>