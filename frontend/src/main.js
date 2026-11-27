import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import { loadConfig } from './config'
import './styles/global.css'

const app = createApp(App)

// 注册全部 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 先加载运行时配置（app-config.json + localStorage 覆盖），再挂载应用
// 注意：配置在任何 axios 请求发出前就绪，避免首次请求打到默认地址
const bootstrap = async () => {
  await loadConfig()
  app.mount('#app')
}
bootstrap()
