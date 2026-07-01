<!--
  AdminDashboard.vue - 管理端首页/工作台

  作用：作为管理端登录后的默认首页，提供三个快捷导航卡片：
    1. 用户管理 -> 跳转到 /admin/users（管理租客和房东账号）
    2. 房源管理 -> 跳转到 /admin/houses（审核和管理所有房源）
    3. 订单管理 -> 跳转到 /admin/orders（查看所有租房订单）

  数据来源：
    cards 数组静态定义，每项包含标题、描述、图标和路由路径。
-->

<template>
  <div>
    <h3>管理员工作台</h3>
    <!-- 快捷导航卡片区域：三列布局，通过 v-for 遍历 cards 数组渲染 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8" v-for="item in cards" :key="item.title">
        <el-card shadow="hover" @click="$router.push(item.path)" style="cursor:pointer;margin-bottom:16px">
          <div class="card"><span class="icon">{{ item.icon }}</span><div><div class="title">{{ item.title }}</div><div class="desc">{{ item.desc }}</div></div></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
/**
 * AdminDashboard.vue - 管理端首页逻辑
 *
 * 定义三个快捷导航卡片的元数据，点击卡片跳转到对应的管理功能页面。
 * 管理员拥有系统最高权限，可以管理所有用户、房源和订单。
 */
const cards = [
  { title: '用户管理', desc: '管理租客和房东账号', icon: '👤', path: '/admin/users' },
  { title: '房源管理', desc: '审核和管理所有房源', icon: '🏠', path: '/admin/houses' },
  { title: '订单管理', desc: '查看所有租房订单', icon: '📋', path: '/admin/orders' },
]
</script>

<style scoped>
.card { display:flex; align-items:center; gap:16px; padding:10px 0; }
.icon { font-size:28px; }
.title { font-size:15px; font-weight:bold; color:#303133; }
.desc { font-size:12px; color:#909399; margin-top:4px; }
</style>
