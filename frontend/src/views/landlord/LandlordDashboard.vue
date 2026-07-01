<!--
  LandlordDashboard.vue - 房东首页/仪表盘

  作用：作为房东端登录后的默认首页，提供两个快捷导航卡片：
    1. 我的房源 -> 跳转到 /landlord/houses（房源管理）
    2. 订单管理 -> 跳转到 /landlord/orders（订单处理）

  交互方式：
    点击卡片直接通过 $router.push 导航到对应子路由页面。
-->

<template>
  <div>
    <h3>房东首页</h3>
    <!-- 快捷导航卡片区域：两列布局 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <!-- 我的房源卡片：点击跳转到房源管理页 -->
        <el-card shadow="hover" @click="$router.push('/landlord/houses')" style="cursor:pointer">
          <div class="card"><span class="icon">🏠</span><div><div class="title">我的房源</div><div class="desc">发布和管理房源信息</div></div></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <!-- 订单管理卡片：点击跳转到订单处理页 -->
        <el-card shadow="hover" @click="$router.push('/landlord/orders')" style="cursor:pointer">
          <div class="card"><span class="icon">📋</span><div><div class="title">订单管理</div><div class="desc">处理租客的租房订单</div></div></div>
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
