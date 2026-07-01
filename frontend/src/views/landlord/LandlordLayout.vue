<!--
  LandlordLayout.vue - 房东端整体布局组件

  作用：作为房东端所有页面的外层容器，提供固定的侧边导航栏和顶部栏结构。
       使用 Element Plus 的 el-container 布局体系实现：
         - el-aside（左侧导航区）：展示房东端菜单（首页/我的房源/订单管理）
         - el-header（顶部栏）：显示当前登录房东姓名和退出按钮
         - el-main（主内容区）：嵌套 <router-view /> 渲染子路由页面

  数据来源：
    - 从 localStorage 读取当前用户信息用于顶部栏展示

  导航方式：
    - el-menu 启用 router 属性，点击菜单项时自动路由跳转
-->

<template>
  <el-container style="height:100vh">
    <!-- 左侧导航栏（固定宽度 200px，深色背景） -->
    <el-aside width="200px" style="background:#2c3e50">
      <!-- 顶部品牌标识区 -->
      <div style="height:60px;line-height:60px;text-align:center;color:#fff;font-size:18px;font-weight:bold;background:#1a252f">🏘️ 房东端</div>
      <!-- 侧边导航菜单 -->
      <el-menu :default-active="route.path" router background-color="#2c3e50" text-color="#bfcbd9" active-text-color="#42b983">
        <el-menu-item index="/landlord/dashboard">📊 首页</el-menu-item>
        <el-menu-item index="/landlord/houses">🏠 我的房源</el-menu-item>
        <el-menu-item index="/landlord/orders">📋 订单管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <!-- 顶部栏：欢迎语 + 退出按钮 -->
      <el-header style="display:flex;align-items:center;justify-content:space-between;background:#fff;border-bottom:1px solid #e6e6e6">
        <span>欢迎，{{ user?.realName }} 房东</span>
        <el-button type="danger" size="small" @click="logout">退出</el-button>
      </el-header>
      <!-- 主内容区：嵌套子路由组件在此渲染 -->
      <el-main style="background:#f0f2f5;padding:20px"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
/**
 * LandlordLayout.vue - 房东端布局逻辑
 *
 * 从 localStorage 读取当前登录用户信息，提供退出登录功能。
 * 退出时清除 localStorage 中的用户数据并跳转到登录页。
 */
import { useRouter, useRoute } from 'vue-router'
const router = useRouter(); const route = useRoute()
// 读取 localStorage 中的用户信息（登录时由 Login.vue 存入）
const user = JSON.parse(localStorage.getItem('user') || 'null')
/** 退出登录：清除本地用户数据并跳转回登录页 */
/** 退出登录：清除本地用户数据并强制跳转到登录页 */
const logout = () => { localStorage.removeItem('user'); window.location.href = '/login' }
</script>
