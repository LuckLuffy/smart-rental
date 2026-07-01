<!--
  OrderList.vue（管理端）- 订单管理页面

  作用：
    1. 以分页表格展示系统中所有的订单（不区分房东/租客）
    2. 管理员可以查看所有订单的完整信息（房源/租客/房东/日期/金额/状态）
    3. 管理员仅拥有删除订单的权限（订单状态变更由房东处理）

  与房东端 OrdersManage.vue 的区别：
    - 管理端展示所有订单（不按 landlordId 筛选）
    - 管理端仅可删除订单，不能变更订单状态
    - 管理端表格额外包含 landlordId（房东ID）列

  关键接口调用：
    - orderApi.page   -> 分页查询所有订单（不传筛选条件）
    - orderApi.delete -> 删除订单
-->

<template>
  <div>
    <el-card>
      <template #header><h4>订单管理</h4></template>
      <!-- 订单列表表格：展示所有字段 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="houseId" label="房源ID" width="80" />
        <el-table-column prop="tenantId" label="租客ID" width="80" />
        <el-table-column prop="landlordId" label="房东ID" width="80" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="100" />
        <!-- 状态列 -->
        <el-table-column prop="status" label="状态" width="100"><template #default="{row}"><el-tag :type="{'PENDING':'warning','CONFIRMED':'success','CANCELLED':'info','DONE':''}[row.status]">{{ {'PENDING':'待确认','CONFIRMED':'租赁中','CANCELLED':'已取消','DONE':'已退租'}[row.status] }}</el-tag></template></el-table-column>
        <!-- 管理员仅有删除操作权限 -->
        <el-table-column label="操作" width="80"><template #default="{row}"><el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button></template></el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <div style="margin-top:16px;text-align:right"><el-pagination v-model:current-page="current" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="fetchData" /></div>
    </el-card>
  </div>
</template>

<script setup>
/**
 * OrderList.vue（管理端）- 订单管理逻辑
 *
 * 管理员以只读方式查看所有订单，仅拥有删除权限。
 * 订单状态变更由房东端（OrdersManage.vue）处理。
 */
import { ref, onMounted } from 'vue'
import { orderApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

// ========== 响应式数据 ==========
const loading = ref(false)            // 表格加载状态
const tableData = ref([])             // 订单列表数据
const current = ref(1)                // 当前页码
const size = ref(10)                  // 每页条数
const total = ref(0)                  // 总记录数

/**
 * 分页查询所有订单
 * 不传任何筛选条件，查询系统所有订单（管理员特权）
 */
const fetchData = async () => {
  loading.value = true
  try { const res = await orderApi.page({ current: current.value, size: size.value }); if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total } } finally { loading.value = false }
}

/**
 * 删除订单
 * 先弹出确认框，用户确认后调用 orderApi.delete
 */
const handleDelete = async (id) => { await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' }); await orderApi.delete(id); ElMessage.success('删除成功'); fetchData() }

onMounted(fetchData)
</script>
