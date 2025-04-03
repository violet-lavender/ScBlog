const {defineConfig} = require('@vue/cli-service')
const webpack = require("webpack")

module.exports = defineConfig({
	transpileDependencies: true,
	publicPath: "/",

	devServer: {
		port: process.env.VUE_APP_PORT, // 端口号

		proxy: {
			'/api': {
				target: process.env.VUE_APP_API_URL, // 后端接口(gateway端口号)
				changeOrigin: true, // 是否跨域
				pathRewrite: {
					'^/api': ''
				},
			},
		},
	},

	configureWebpack: {
		externals: {
			// CDN 的 Element 依赖全局变量 Vue， 所以 Vue 也需要使用 CDN 引入
			'vue': 'Vue',
			// 属性名称 element-ui, 表示遇到 import xxx from 'element-ui' 这类引入 'element-ui'的，
			// 不去 node_modules 中找，而是去找 全局变量 ELEMENT
			'element-ui': 'ELEMENT',
			'axios': 'axios',
			'vuex': 'Vuex',
			'vue-router': 'VueRouter',
		},
	},
});
