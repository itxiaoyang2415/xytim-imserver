import axios from 'axios'

let createHttpRequest = function(baseUrl, loginInfo){
	if (window.plus) {
		// IOS系统必须使用plus.net.XMLHttpRequest，否则请求会报错
		window.XMLHttpRequest = window.plus.net.XMLHttpRequest;
	}

	const http = axios.create({
		baseURL: baseUrl,
		timeout: 1000 * 30,
		headers: {
			'Content-Type': 'application/json; charset=utf-8'
		}
	})

	/**
	 * 请求拦截
	 */
	http.interceptors.request.use(config => {
		let accessToken = loginInfo.accessToken;
		if (accessToken) {
			config.headers.accessToken = encodeURIComponent(accessToken);
		}
		return config
	}, error => {
		return Promise.reject(error)
	})

	/**
	 * 响应拦截
	 */
	http.interceptors.response.use(async response => {
		if (response.data.code == 200) {
			return response.data.data;
		} else if (response.data.code == 401) {
			console.log("token失效，尝试重新获取")
			let refreshToken = loginInfo.refreshToken;
			// 发送请求, 进行刷新token操作, 获取新的token
			loginInfo = await http({
				method: 'put',
				url: '/refreshToken',
				headers: {
					refreshToken: refreshToken
				}
			}).catch(() => {
				console.log("服务器请求异常")
			})
			// 这里需要把headers清掉，否则请求时会报错，原因暂不详...
			if (typeof response.config.data != 'object') {
				response.config.headers = undefined;
			}
			// 重新发送刚才的请求
			return http(response.config)
		} else {
			return Promise.reject(response.data)
		}
	}, error => {
		console.log('服务器出了点小差，请稍后再试')
		return Promise.reject(error)
	})
	return http;
}


export default createHttpRequest