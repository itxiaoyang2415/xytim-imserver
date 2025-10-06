import Vue from 'vue'
import App from './App.vue'
import * as enums from './common/enums';
import './assets/iconfont/iconfont.css';
// 开发时打开
//import VConsole from './vconsole' 

Vue.prototype.$enums = enums
Vue.config.productionTip = false

new Vue({
	render: h => h(App)
}).$mount('#app')