<!--
  App.vue - 智慧租房管理系统根组件

  作用：作为 Vue 应用的入口视图，仅提供一个 <router-view /> 路由出口。
        所有页面（登录页、租客/房东/管理端各页面）均由 vue-router 根据
        当前 URL 动态渲染到 <router-view /> 中。

  全局样式说明：
    - 重置所有元素的外边距和内边距、统一盒模型为 border-box
    - html / body / #app 全屏高度，确保 Element Plus 的 el-container
      等组件可以占满整个视口高度
-->
<template>
  <!-- 路由出口：匹配当前 URL 的页面组件将渲染在这里 -->
  <router-view />
</template>

<style>
/* 全局样式重置：确保所有元素的基础样式一致 */
* { margin: 0; padding: 0; box-sizing: border-box; }
/* 让根元素和 Vue 挂载点占据整个视口高度，方便子组件使用 height: 100% */
html, body, #app { height: 100%; }
</style>
