<!--
  MyOrders.vue（租客端）- 我的订单页面

  作用：
    以分页表格形式展示当前登录租客的所有租房订单。
    订单状态通过不同颜色的 el-tag 标签呈现：
      - PENDING（待确认）：黄色警告色
      - CONFIRMED（租赁中）：绿色成功色
      - CANCELLED（已取消）：灰色信息色
      - DONE（已退租）：默认色

  数据来源：
    调用 orderApi.page 并传入选中的租客 ID（tenantId），
    后端返回该租客的所有订单记录。

  交互：
    本页面为只读视图，租客在此查看订单状态，不提供修改操作。
    状态变更由房东端（OrdersManage.vue）完成。
-->

<template>
  <div>
    <el-card>
      <template #header><h4>我的订单</h4></template>
      <!-- 订单列表表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="houseId" label="房源ID" width="80" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="100" />
        <!-- 状态列：通过映射表显示中文标签并配置对应颜色 -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}"><el-tag :type="{'PENDING':'warning','CONFIRMED':'success','CANCELLED':'info','DONE':''}[row.status]">{{ {'PENDING':'待确认','CONFIRMED':'租赁中','CANCELLED':'已取消','DONE':'已退租'}[row.status] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
      </el-table>
      <!-- 分页组件 -->
      <div style="margin-top:16px;text-align:right"><el-pagination v-model:current-page="current" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="fetchData" /></div>
    </el-card>
  </div>
</template>

<script setup>
/**
 * MyOrders.vue（租客端）- 我的订单逻辑
 */
import { ref, onMounted } from 'vue'
import { orderApi } from '../../api'
const user = JSON.parse(localStorage.getItem('user') || 'null')
const loading = ref(false), tableData = ref([]), current = ref(1), size = ref(10), total = ref(0)

/**
 * 分页查询当前租客的订单
 * 调用 orderApi.page，传入 tenantId 筛选属于该租客的订单
 */
const fetchData = async () => {
  loading.value = true
  try { const res = await orderApi.page({ current: current.value, size: size.value, tenantId: user.id }); if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total } } finally { loading.value = false }
}
onMounted(fetchData)
</script>
