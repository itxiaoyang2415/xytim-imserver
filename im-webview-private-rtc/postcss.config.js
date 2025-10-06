module.exports = {
	plugins: {
		"postcss-import": {},
		autoprefixer: {
			path: ["./src/*"],
		},
		"postcss-px-to-viewport": {
			viewportWidth: 750, // 视窗的宽度，对应设计稿的宽度
			unitPrecision: 5, // 指定px转换为视窗单位值的小数位数(因为无法整除)
			viewportUnit: "vw", // 指定需要转换成的视窗单位，使用vw
			fontViewportUnit: 'vw', // 字体使用的视口单位
			unitPrecision: 13, // 指定`px`转换为视窗单位值的小数后 x位数
			propList: ['*'], // 能转化为 rem的属性列表
			selectorBlackList: [], //指定不转换为视窗单位的类，可以自定义，可以无限添加,建议定义一至两个通用的类名
			minPixelValue: 1, // 小于或等于`1px`不转换为视窗单位，你也可以设置为你想要的值
			mediaQuery: false, // 允许在媒体查询中转换
			replace: true, //是否直接更换属性值，而不添加备用属性
			landscape: true,
			landscapeUnit:'vw',  //横屏时使用的视口宽度
			landscapeWidth: 1800,
			exclude: /(\/|\\)(node_modules)(\/|\\)/ // 不包含node_modules文件
		},
	},
}