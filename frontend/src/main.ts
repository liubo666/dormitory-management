import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useUserStore } from '@/stores/user'

// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 样式
import '@/assets/styles/index.css'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  size: 'default'
})

// 在应用挂载前初始化用户信息
const initApp = async () => {
  const userStore = useUserStore()

  // 如果本地有token，尝试初始化用户信息
  if (userStore.token) {
    try {
      await userStore.initUserInfo()
    } catch (error) {
      // 如果初始化失败，说明token可能已过期，userStore会自动处理
      console.warn('用户信息初始化失败，可能需要重新登录')
    }
  }

  app.mount('#app')
}

initApp()