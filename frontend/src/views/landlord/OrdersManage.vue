<!--
  OrdersManage.vue（房东端）- 订单管理页面

  作用：
    1. 以分页表格展示当前房东收到的所有租房订单
    2. 房东可根据订单状态执行不同的操作：
       - PENDING（待确认）-> 可"确认"（CONFIRMED）或"拒绝"（CANCELLED）
       - CONFIRMED（租赁中）-> 可"退租"（DONE）
       - CANCELLED（已取消）或 DONE（已退租）-> 无操作按钮

  状态流转说明：
    PENDING（租客下单） -> 房东确认 -> CONFIRMED（租赁中）
    PENDING（租客下单） -> 房东拒绝 -> CANCELLED（已取消）
    CONFIRMED（租赁中） -> 房东退租 -> DONE（已退租）

  关键接口调用：
    - orderApi.page   -> 分页查询当前房东的订单
    - orderApi.update -> 更新订单状态（确认/拒绝/退租）
-->

<template>
  <div>
    <el-card>
      <template #header><h4>订单管理</h4></template>
      <!-- 订单列表表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="houseId" label="房源ID" width="80" />
        <el-table-column prop="tenantId" label="租客ID" width="80" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="100" />
        <!-- 状态列：显示中文标签和对应颜色 -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}"><el-tag :type="{'PENDING':'warning','CONFIRMED':'success','CANCELLED':'info','DONE':''}[row.status]">{{ {'PENDING':'待确认','CONFIRMED':'租赁中','CANCELLED':'已取消','DONE':'已退租'}[row.status] }}</el-tag></template>
        </el-table-column>
        <!-- 操作列：根据状态动态显示可用的操作按钮 -->
        <el-table-column label="操作" width="180"><template #default="{row}">
          <el-button v-if="row.status==='PENDING'" type="success" size="small" @click="updateStatus(row,'CONFIRMED')">确认</el-button>
          <el-button v-if="row.status==='PENDING'" type="danger" size="small" @click="updateStatus(row,'CANCELLED')">拒绝</el-button>
          <el-button v-if="row.status==='CONFIRMED'" type="primary" size="small" @click="updateStatus(row,'DONE')">退租</el-button>
        </template></el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <div style="margin-top:16px;text-align:right"><el-pagination v-model:current-page="current" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="fetchData" /></div>
    </el-card>
  </div>
</template>

<script setup>
/**
 * OrdersManage.vue（房东端）- 订单管理逻辑
 *
 * 房东在此页面处理租客的租房请求，核心操作是变更订单状态。
 * 订单状态流转：PENDING -> CONFIRMED -> DONE 或 PENDING -> CANCELLED
 */
import { ref, onMounted } from 'vue'
import { orderApi } from '../../api'
import { ElMessage } from 'element-plus'
const user = JSON.parse(localStorage.getItem('user') || 'null')  // 当前登录房东

// ========== 响应式数据 ==========
const loading = ref(false)            // 表格加载状态
const tableData = ref([])             // 订单列表数据
const current = ref(1)                // 当前页码
const size = ref(10)                  // 每页条数
const total = ref(0)                  // 总记录数

/**
 * 分页查询当前房东的订单
 * 调用 orderApi.page 并传入 landlordId 筛选属于该房东的订单
 */
const fetchData = async () => {
  loading.value = true
  try { const res = await orderApi.page({ current: current.value, size: size.value, landlordId: user.id }); if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total } } finally { loading.value = false }
}

/**
 * 更新订单状态
 * @param {Object} row   - 当前操作的订单行数据
 * @param {string} status - 目标状态（CONFIRMED / CANCELLED / DONE）
 * 将订单的 status 字段更新为目标状态，完成确认/拒绝/退租操作
 */
const updateStatus = async (row, status) => {
  await orderApi.update({ ...row, status })
  ElMessage.success('操作成功'); fetchData()
}

onMounted(fetchData)
</script>
