import Vue from 'vue'
import App from './App.vue'
import * as enums from './common/enums';
import './assets/iconfont/iconfont.css';
import Vant from 'vant'
import VantGreen from 'vant-green';
import 'vant-green/lib/index.css';
// 开发时打开
//import VConsole from './vconsole'
Vue.use(VantGreen);
Vue.prototype.$enums = enums
Vue.config.productionTip = false

new Vue({

	render: h => h(App)
}).$mount('#app')