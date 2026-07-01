/**
 * 智慧租房管理系统 - 前端入口文件
 *
 * 作用：初始化 Vue 应用实例，注册 Element Plus 组件库（中文语言包），
 *       加载路由配置，并将整个应用挂载到 HTML 中的 #app 容器上。
 *
 * 执行顺序：import 依赖 -> 创建 Vue 应用 -> 注册插件 -> 挂载渲染
 */

import { createApp } from 'vue'             // Vue 3 核心，用于创建应用实例
import ElementPlus from 'element-plus'       // Element Plus UI 组件库
import 'element-plus/dist/index.css'         // Element Plus 默认样式
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'  // 中文语言包（组件内部的文字显示为中文）
import App from './App.vue'                  // 根组件（仅包含 <router-view />）
import router from './router'                // 路由配置（含导航守卫）

// 创建 Vue 应用实例 -> 注册 Element Plus（启用中文）-> 注册路由 -> 挂载到 DOM
const app = createApp(App)
app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.mount('#app')
