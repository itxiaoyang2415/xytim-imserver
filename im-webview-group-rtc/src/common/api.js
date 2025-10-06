import createHttpRequest from './request'

class ImApi {
	constructor(baseUrl, loginInfo) {
		this.http = createHttpRequest(baseUrl, loginInfo);
	}
}

ImApi.prototype.findGroupMembers = function(groupId) {
	return this.http({
		url: '/group/members/' + groupId,
		method: 'get'
	})
}

ImApi.prototype.setup = function(groupId, userInfos) {
	let formData = {
		groupId,
		userInfos
	}
	return this.http({
		url: '/webrtc/group/setup',
		method: 'post',
		data: formData
	})
}

ImApi.prototype.accept = function(groupId) {
	return this.http({
		url: '/webrtc/group/accept?groupId=' + groupId,
		method: 'post'
	})
}

ImApi.prototype.reject = function(groupId) {
	return this.http({
		url: '/webrtc/group/reject?groupId=' + groupId,
		method: 'post'
	})
}

ImApi.prototype.failed = function(groupId, reason) {
	let formData = {
		groupId,
		reason
	}
	return this.http({
		url: '/webrtc/group/failed',
		method: 'post',
		data: formData
	})
}


ImApi.prototype.join = function(groupId) {
	return this.http({
		url: '/webrtc/group/join?groupId=' + groupId,
		method: 'post'
	})
}

ImApi.prototype.invite = function(groupId, userInfos) {
	let formData = {
		groupId,
		userInfos
	}
	return this.http({
		url: '/webrtc/group/invite',
		method: 'post',
		data: formData
	})
}


ImApi.prototype.offer = function(groupId, userId, offer) {
	let formData = {
		groupId,
		userId,
		offer
	}
	return this.http({
		url: '/webrtc/group/offer',
		method: 'post',
		data: formData
	})
}

ImApi.prototype.answer = function(groupId, userId, answer) {
	let formData = {
		groupId,
		userId,
		answer
	}
	return this.http({
		url: '/webrtc/group/answer',
		method: 'post',
		data: formData
	})
}

ImApi.prototype.quit = function(groupId) {
	return this.http({
		url: '/webrtc/group/quit?groupId=' + groupId,
		method: 'post'
	})
}

ImApi.prototype.cancel = function(groupId) {
	return this.http({
		url: '/webrtc/group/cancel?groupId=' + groupId,
		method: 'post'
	})
}

ImApi.prototype.candidate = function(groupId, userId, candidate) {
	let formData = {
		groupId,
		userId,
		candidate
	}
	return this.http({
		url: '/webrtc/group/candidate',
		method: 'post',
		data: formData
	})
}

ImApi.prototype.device = function(groupId, isCamera, isMicroPhone, isShareScreen) {
	let formData = {
		groupId,
		isCamera,
		isMicroPhone,
		isShareScreen
	}
	return this.http({
		url: '/webrtc/group/device',
		method: 'post',
		data: formData
	})
}


ImApi.prototype.candidate = function(groupId, userId, candidate) {
	let formData = {
		groupId,
		userId,
		candidate
	}
	return this.http({
		url: '/webrtc/group/candidate',
		method: 'post',
		data: formData
	})
}

ImApi.prototype.heartbeat = function(groupId) {
	return this.http({
		url: '/webrtc/group/heartbeat?groupId=' + groupId,
		method: 'post'
	})
}


export default ImApi;