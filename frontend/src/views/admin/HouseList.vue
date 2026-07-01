<!--
  HouseList.vue（管理端）- 房源管理页面

  作用：
    1. 以分页表格展示系统中所有的房源（不区分房东）
    2. 管理员可编辑任意房源的信息和状态
    3. 管理员可删除任意房源

  与房东端 MyHouses.vue 的区别：
    - 管理端展示所有房东的房源（不按 landlordId 筛选）
    - 管理端可以修改房源状态（待租/已租/下架）
    - 管理端没有"新增房源"功能（房源由房东自行发布）

  关键接口调用：
    - houseApi.page   -> 分页查询所有房源（不传 landlordId）
    - houseApi.update -> 编辑房源
    - houseApi.delete -> 删除房源
-->

<template>
  <div>
    <el-card>
      <template #header><div style="display:flex;justify-content:space-between"><h4>房源管理</h4></div></template>
      <!-- 房源列表表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="area" label="面积" width="80" />
        <el-table-column prop="price" label="月租" width="80" />
        <el-table-column prop="type" label="户型" width="100" />
        <el-table-column prop="landlordId" label="房东ID" width="80" />
        <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='AVAILABLE'?'success':'info'">{{ row.status==='AVAILABLE'?'待租':'已租' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="150"><template #default="{row}"><el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button><el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button></template></el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <div style="margin-top:16px;text-align:right"><el-pagination v-model:current-page="current" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="fetchData" /></div>
    </el-card>

    <!-- 编辑房源对话框：管理员可以修改房源信息及状态 -->
    <el-dialog v-model="dialogVisible" title="编辑房源" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="面积"><el-input-number v-model="form.area" /></el-form-item>
        <el-form-item label="月租"><el-input-number v-model="form.price" /></el-form-item>
        <el-form-item label="户型"><el-input v-model="form.type" /></el-form-item>
        <!-- 管理端特有：可以修改房源状态（待租/已租/下架） -->
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="待租" value="AVAILABLE" /><el-option label="已租" value="RENTED" /><el-option label="下架" value="OFFLINE" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * HouseList.vue（管理端）- 房源管理逻辑
 *
 * 管理员对所有房源拥有编辑和删除权限，但无法新增房源。
 * 编辑时可修改房源状态（待租/已租/下架），这是管理员特有的能力。
 */
import { ref, reactive, onMounted } from 'vue'
import { houseApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

// ========== 响应式数据 ==========
const loading = ref(false)            // 表格加载状态
const tableData = ref([])             // 房源列表数据
const current = ref(1)                // 当前页码
const size = ref(10)                  // 每页条数
const total = ref(0)                  // 总记录数
const dialogVisible = ref(false)      // 编辑对话框是否可见
// 房源编辑表单（管理端不需要 landlordId 字段）
const form = reactive({ id: null, title: '', address: '', description: '', area: 0, price: 0, type: '', status: 'AVAILABLE' })

/**
 * 分页查询所有房源
 * 不传 landlordId，查询系统所有房源（管理员特权）
 */
const fetchData = async () => {
  loading.value = true
  try { const res = await houseApi.page({ current: current.value, size: size.value }); if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total } } finally { loading.value = false }
}

/**
 * 打开编辑房源对话框
 * 将选中行的数据填充到表单中
 */
const openDialog = (row) => { if (row) Object.assign(form, row); dialogVisible.value = true }

/**
 * 保存房源修改（仅更新操作）
 * 管理员端只能编辑已有房源，不能新增
 */
const save = async () => {
  await houseApi.update(form); ElMessage.success('保存成功'); dialogVisible.value = false; fetchData()
}

/**
 * 删除房源
 * 先弹出确认框，用户确认后调用 houseApi.delete
 */
const handleDelete = async (id) => { await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' }); await houseApi.delete(id); ElMessage.success('删除成功'); fetchData() }

onMounted(fetchData)
</script>
