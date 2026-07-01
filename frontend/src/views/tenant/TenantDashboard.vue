<!--
  TenantDashboard.vue - 租客首页/仪表盘

  作用：作为租客端登录后的默认首页，提供两个快捷导航卡片：
    1. 浏览房源 -> 跳转到 /tenant/houses（房源列表）
    2. 我的订单 -> 跳转到 /tenant/orders（订单列表）

  交互方式：
    点击卡片直接通过 $router.push 导航到对应子路由页面，
    卡片使用 hover 阴影效果提升点击感。
-->

<template>
  <div>
    <h3>租客首页</h3>
    <!-- 快捷导航卡片区域：两列布局 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <!-- 浏览房源卡片：点击跳转到房源列表页 -->
        <el-card shadow="hover" @click="$router.push('/tenant/houses')" style="cursor:pointer">
          <div class="card"><span class="icon">🔍</span><div><div class="title">浏览房源</div><div class="desc">查看所有在租房源</div></div></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <!-- 我的订单卡片：点击跳转到订单列表页 -->
        <el-card shadow="hover" @click="$router.push('/tenant/orders')" style="cursor:pointer">
          <div class="card"><span class="icon">📋</span><div><div class="title">我的订单</div><div class="desc">查看我的租房订单</div></div></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.card { display:flex; align-items:center; gap:16px; padding:10px 0; }
.icon { font-size:32px; }
.title { font-size:16px; font-weight:bold; color:#303133; }
.desc { font-size:13px; color:#909399; margin-top:4px; }
</style>
