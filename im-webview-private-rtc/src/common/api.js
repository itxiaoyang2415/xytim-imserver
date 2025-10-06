import createHttpRequest from './request'

class ImApi {
	constructor(baseUrl, loginInfo) {
		this.http = createHttpRequest(baseUrl, loginInfo);
	}
}


ImApi.prototype.setup = function(uid, mode) {
	return this.http({
		url: `/webrtc/private/setup?uid=${uid}&mode=${mode}`,
		method: 'post'
	})
}

ImApi.prototype.accept = function(uid) {
	return this.http({
		url: `/webrtc/private/accept?uid=${uid}`,
		method: 'post'
	})
}


ImApi.prototype.handup = function(uid) {
	return this.http({
		url: `/webrtc/private/handup?uid=${uid}`,
		method: 'post'
	})
}

ImApi.prototype.cancel = function(uid) {
	return this.http({
		url: `/webrtc/private/cancel?uid=${uid}`,
		method: 'post'
	})
}

ImApi.prototype.reject = function(uid) {
	return this.http({
		url: `/webrtc/private/reject?uid=${uid}`,
		method: 'post'
	})
}

ImApi.prototype.failed = function(uid, reason) {
	return this.http({
		url: `/webrtc/private/failed?uid=${uid}&reason=${reason}`,
		method: 'post'
	})
}

ImApi.prototype.offer = function(uid, offer) {
	return this.http({
		url: `/webrtc/private/offer?uid=${uid}`,
		method: 'post',
		data: JSON.stringify(offer)
	});
}

ImApi.prototype.answer = function(uid, answer) {
	return this.http({
		url: `/webrtc/private/answer?uid=${uid}`,
		method: 'post',
		data: JSON.stringify(answer)
	});
}

ImApi.prototype.sendCandidate = function(uid, candidate) {
	return this.http({
		url: `/webrtc/private/candidate?uid=${uid}`,
		method: 'post',
		data: JSON.stringify(candidate)
	});
}

ImApi.prototype.heartbeat = function(uid) {
	return this.http({
		url: `/webrtc/private/heartbeat?uid=${uid}`,
		method: 'post'
	})
}

export default ImApi;