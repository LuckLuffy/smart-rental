<!--
  HouseList.vue（租客端）- 房源浏览与租房下单页面

  作用：
    1. 以表格分页展示所有"待租"（AVAILABLE）状态的房源
    2. 提供按标题搜索功能
    3. 租客点击"租房"按钮弹出对话框，选择起止日期后提交订单

  交互流程：
    进入页面 -> onMounted 触发 fetchData 加载房源列表
    点击租房 -> 弹出确认租房对话框 -> 填写日期 -> 调用 orderApi.save 创建订单
    -> 成功后关闭对话框，订单状态为 PENDING（待房东确认）

  关键接口调用：
    - houseApi.page   -> 分页查询房源（仅展示 AVAILABLE 状态的房源）
    - orderApi.save   -> 创建新订单（status: PENDING）
-->

<template>
  <div>
    <el-card>
      <template #header><h4>房源列表</h4></template>
      <!-- 搜索栏：按房源标题模糊搜索 -->
      <el-form :inline="true"><el-form-item><el-input v-model="search" placeholder="搜索房源" clearable /></el-form-item><el-form-item><el-button type="primary" @click="fetchData">搜索</el-button></el-form-item></el-form>
      <!-- 房源数据表格：仅展示待租房源，已租房源不可操作 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="area" label="面积(㎡)" width="80" />
        <el-table-column prop="price" label="月租(元)" width="100"><template #default="{row}"><span style="color:#f56c6c;font-weight:bold">¥{{ row.price }}</span></template></el-table-column>
        <el-table-column prop="type" label="户型" width="100" />
        <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='AVAILABLE'?'success':'info'">{{ row.status==='AVAILABLE'?'待租':'已租' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="100"><template #default="{row}"><el-button type="primary" size="small" @click="rent(row)" :disabled="row.status!=='AVAILABLE'">租房</el-button></template></el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <div style="margin-top:16px;text-align:right"><el-pagination v-model:current-page="current" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="fetchData" /></div>
    </el-card>

    <!-- 确认租房弹窗：选择租赁起止日期 -->
    <el-dialog v-model="dialogVisible" title="确认租房" width="400px">
      <p>确定要租 <b>{{ form.title }}</b> 吗？</p>
      <el-form :model="form" label-width="80px" style="margin-top:16px">
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submitRent">确认下单</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * HouseList.vue（租客端）- 房源浏览与租房下单逻辑
 */
import { ref, reactive, onMounted } from 'vue'
import { houseApi, orderApi } from '../../api'
import { ElMessage } from 'element-plus'

// ========== 响应式数据 ==========
const loading = ref(false)            // 表格加载状态
const tableData = ref([])             // 分页查询返回的房源数据
const current = ref(1)                // 当前页码
const size = ref(10)                  // 每页条数
const total = ref(0)                  // 总记录数
const search = ref('')                // 搜索关键词
const dialogVisible = ref(false)      // 租房弹出框是否可见
const user = JSON.parse(localStorage.getItem('user') || 'null')  // 当前登录租客信息
// 租房表单数据：房源标题、ID、房东ID、起止日期
const form = reactive({ id: null, title: '', houseId: null, landlordId: null, startDate: '', endDate: '' })

/**
 * 分页查询待租房源
 * - 调用 houseApi.page 并传入搜索条件（搜索关键词 + 状态固定为 AVAILABLE）
 * - 将后端返回的数据填充到 tableData 和 total 中
 */
const fetchData = async () => {
  loading.value = true
  try {
    const res = await houseApi.page({ current: current.value, size: size.value, title: search.value, status: 'AVAILABLE' })
    if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

/**
 * 打开租房弹窗
 * 将选中的房源信息填入表单，并弹出日期选择对话框
 */
const rent = (row) => {
  form.id = null; form.title = row.title; form.houseId = row.id; form.landlordId = row.landlordId
  form.startDate = ''; form.endDate = ''
  dialogVisible.value = true
}

/**
 * 提交租房订单
 * 1. 校验起止日期是否已选择
 * 2. 调用 orderApi.save 创建订单（订单初始状态为 PENDING，等待房东确认）
 * 3. 成功后关闭对话框并显示成功提示
 */
const submitRent = async () => {
  if (!form.startDate || !form.endDate) { ElMessage.warning('请选择起止日期'); return }
  try {
    const res = await orderApi.save({
      houseId: form.houseId,
      tenantId: user.id,
      landlordId: form.landlordId,
      startDate: form.startDate,
      endDate: form.endDate,
      totalAmount: 0,                // 总金额由后端计算或后续更新
      status: 'PENDING'              // 初始状态：待房东确认
    })
    if (res.code === 200) {
      ElMessage.success('下单成功，等待房东确认')
      dialogVisible.value = false
    } else {
      ElMessage.error(res.message || '下单失败')
    }
  } catch (e) {
    ElMessage.error('下单失败，请检查网络连接')
  }
}

// 页面挂载时加载第一页数据
onMounted(fetchData)
</script>
