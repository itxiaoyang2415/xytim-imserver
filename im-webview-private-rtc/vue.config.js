module.exports = {
	devServer: {
		proxy: {
			'/api': {
				target: 'http://127.0.0.1:8888',
				changeOrigin: true,
				ws: false,
				pathRewrite: {
					'^/api': ''
				}
			}
		}
	},
	publicPath: "",
	// 打包到uniapp的webview专属目录
	outputDir: '../im-uniapp/hybrid/html/rtc-private'
}