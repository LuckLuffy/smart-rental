<!--
  MyHouses.vue（房东端）- 我的房源管理页面

  作用：
    1. 以分页表格展示当前房东发布的所有房源
    2. 提供"发布房源"功能（新增房源）
    3. 支持对已有房源进行编辑和删除操作
    4. 房源状态默认标记为 AVAILABLE（待租）

  关键接口调用：
    - houseApi.page   -> 分页查询当前房东的房源
    - houseApi.save   -> 新增房源
    - houseApi.update -> 编辑房源
    - houseApi.delete -> 删除房源

  注意：
    - 所有接口都传入了 landlordId = 当前用户 ID，确保只能操作自己的房源
-->

<template>
  <div>
    <el-card>
      <!-- 表头：标题 + 发布房源按钮 -->
      <template #header><div style="display:flex;justify-content:space-between"><h4>我的房源</h4><el-button type="primary" @click="openDialog()">发布房源</el-button></div></template>
      <!-- 房源列表表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="area" label="面积(㎡)" width="80" />
        <el-table-column prop="price" label="月租(元)" width="100" />
        <el-table-column prop="type" label="户型" width="100" />
        <!-- 状态：待租 / 已租 -->
        <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='AVAILABLE'?'success':'info'">{{ row.status==='AVAILABLE'?'待租':'已租' }}</el-tag></template></el-table-column>
        <!-- 操作按钮：编辑 / 删除 -->
        <el-table-column label="操作" width="180"><template #default="{row}"><el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button><el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button></template></el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <div style="margin-top:16px;text-align:right"><el-pagination v-model:current-page="current" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="fetchData" /></div>
    </el-card>

    <!-- 新增/编辑房源对话框（标题根据 form.id 动态切换） -->
    <el-dialog v-model="dialogVisible" :title="form.id?'编辑房源':'发布房源'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="面积(㎡)"><el-input-number v-model="form.area" :min="1" /></el-form-item>
        <el-form-item label="月租(元)"><el-input-number v-model="form.price" :min="0" /></el-form-item>
        <el-form-item label="户型"><el-input v-model="form.type" placeholder="如一室一卫" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * MyHouses.vue（房东端）- 我的房源管理逻辑
 *
 * 核心操作：对房源进行 CRUD（增删改查），所有操作仅限于当前登录房东的房源。
 * 新增时自动填入 landlordId 和 status（AVAILABLE）。
 */
import { ref, reactive, onMounted } from 'vue'
import { houseApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
const user = JSON.parse(localStorage.getItem('user') || 'null')  // 当前登录房东

// ========== 响应式数据 ==========
const loading = ref(false)            // 表格加载状态
const tableData = ref([])             // 房源列表数据
const current = ref(1)                // 当前页码
const size = ref(10)                  // 每页条数
const total = ref(0)                  // 总记录数
const dialogVisible = ref(false)      // 新增/编辑对话框是否可见
// 房源表单数据（新增和编辑共用）
const form = reactive({ id: null, title: '', address: '', description: '', area: 0, price: 0, type: '', landlordId: user.id, status: 'AVAILABLE' })

/**
 * 分页查询当前房东的房源列表
 * 调用 houseApi.page 并传入 landlordId 筛选属于该房东的房源
 */
const fetchData = async () => {
  loading.value = true
  try { const res = await houseApi.page({ current: current.value, size: size.value, landlordId: user.id }); if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total } } finally { loading.value = false }
}

/**
 * 打开新增/编辑房源对话框
 * @param {Object|null} row - 编辑时传入已有房源数据，新增时传 null
 */
const openDialog = (row) => {
  if (row) Object.assign(form, row)  // 编辑：将行数据填充到表单
  else Object.assign(form, { id: null, title: '', address: '', description: '', area: 0, price: 0, type: '', landlordId: user.id, status: 'AVAILABLE' })  // 新增：重置表单
  dialogVisible.value = true
}

/**
 * 保存房源（新增或更新）
 * 根据 form.id 是否存在决定调用 save（新增）还是 update（更新）
 */
const save = async () => {
  if (form.id) await houseApi.update(form); else await houseApi.save(form)
  ElMessage.success('保存成功'); dialogVisible.value = false; fetchData()
}

/**
 * 删除房源
 * 先弹出确认框，用户确认后调用 houseApi.delete
 */
const handleDelete = async (id) => { await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' }); await houseApi.delete(id); ElMessage.success('删除成功'); fetchData() }

onMounted(fetchData)
</script>
