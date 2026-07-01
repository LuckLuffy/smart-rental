<!--
  UserList.vue（管理端）- 用户管理页面

  作用：
    1. 以分页表格展示系统所有用户（管理员/房东/租客）
    2. 提供按角色筛选的搜索功能
    3. 管理员可新增、编辑、删除任意用户

  表结构字段：
    用户名（username）/ 姓名（realName）/ 角色（role：ADMIN/LANDLORD/TENANT）
    / 手机号（phone）/ 性别（gender：1=男/0=女）

  关键接口调用：
    - userApi.page   -> 分页查询用户列表（支持按角色筛选）
    - userApi.save   -> 新增用户
    - userApi.update -> 编辑用户
    - userApi.delete -> 删除用户

  注意：
    编辑时会将密码字段清空，密码只在新增时设置，编辑时不修改密码。
-->

<template>
  <div>
    <el-card>
      <!-- 表头：标题 + 新增用户按钮 -->
      <template #header><div style="display:flex;justify-content:space-between"><h4>用户管理</h4><el-button type="primary" @click="openDialog()">新增用户</el-button></div></template>
      <!-- 搜索栏：按角色筛选 -->
      <el-form :inline="true"><el-form-item><el-select v-model="search.role" placeholder="角色" clearable><el-option label="管理员" value="ADMIN" /><el-option label="房东" value="LANDLORD" /><el-option label="租客" value="TENANT" /></el-select></el-form-item><el-form-item><el-button type="primary" @click="fetchData">查询</el-button></el-form-item></el-form>
      <!-- 用户列表表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="role" label="角色" width="80"><template #default="{row}"><el-tag>{{ {'ADMIN':'管理员','LANDLORD':'房东','TENANT':'租客'}[row.role] }}</el-tag></template></el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="gender" label="性别" width="60"><template #default="{row}">{{ row.gender===1?'男':'女' }}</template></el-table-column>
        <el-table-column label="操作" width="150"><template #default="{row}"><el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button><el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button></template></el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <div style="margin-top:16px;text-align:right"><el-pagination v-model:current-page="current" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="fetchData" /></div>
    </el-card>

    <!-- 新增/编辑用户对话框（标题根据 form.id 动态切换） -->
    <el-dialog v-model="dialogVisible" :title="form.id?'编辑用户':'新增用户'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="角色"><el-select v-model="form.role" style="width:100%"><el-option label="管理员" value="ADMIN" /><el-option label="房东" value="LANDLORD" /><el-option label="租客" value="TENANT" /></el-select></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio :value="1">男</el-radio><el-radio :value="0">女</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * UserList.vue（管理端）- 用户管理逻辑
 *
 * 核心操作：对系统用户进行 CRUD（增删改查）。
 * 管理员可以管理所有角色的用户，支持按角色筛选。
 * 编辑时密码字段留空，表示不修改密码。
 */
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

// ========== 响应式数据 ==========
const loading = ref(false)            // 表格加载状态
const tableData = ref([])             // 用户列表数据
const current = ref(1)                // 当前页码
const size = ref(10)                  // 每页条数
const total = ref(0)                  // 总记录数
const dialogVisible = ref(false)      // 新增/编辑对话框是否可见
const search = reactive({ role: '' }) // 搜索条件：角色筛选
// 用户表单数据（新增和编辑共用）
const form = reactive({ id: null, username: '', password: '', realName: '', role: 'TENANT', phone: '', gender: 1 })

/**
 * 分页查询用户列表
 * 支持按角色筛选（不传 role 时查询全部）
 */
const fetchData = async () => {
  loading.value = true
  try { const res = await userApi.page({ current: current.value, size: size.value, role: search.role }); if (res.code === 200) { tableData.value = res.data.records; total.value = res.data.total } } finally { loading.value = false }
}

/**
 * 打开新增/编辑用户对话框
 * @param {Object|null} row - 编辑时传入已有用户数据，新增时传 null
 * 编辑时密码字段置空（前端不展示已有密码）
 */
const openDialog = (row) => {
  if (row) { Object.assign(form, row); form.password = '' }  // 编辑：填充表单，密码留空
  else Object.assign(form, { id: null, username: '', password: '', realName: '', role: 'TENANT', phone: '', gender: 1 })  // 新增：重置表单
  dialogVisible.value = true
}

/**
 * 保存用户（新增或更新）
 * 根据 form.id 是否存在决定调用 save（新增）还是 update（更新）
 */
const save = async () => {
  if (form.id) await userApi.update(form); else await userApi.save(form)
  ElMessage.success('保存成功'); dialogVisible.value = false; fetchData()
}

/**
 * 删除用户
 * 先弹出确认框，用户确认后调用 userApi.delete
 */
const handleDelete = async (id) => { await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' }); await userApi.delete(id); ElMessage.success('删除成功'); fetchData() }

onMounted(fetchData)
</script>
