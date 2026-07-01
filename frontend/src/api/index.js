/**
 * 智慧租房管理系统 - 后端 API 接口层模块
 *
 * 作用：
 *   1. 基于 axios 创建统一的 HTTP 请求实例，配置基础 URL 和超时时间
 *   2. 注册响应拦截器，自动解包 response.data，让调用方直接获取业务数据
 *   3. 按照业务领域导出三个 API 对象：userApi / houseApi / orderApi，
 *      封装所有与后端交互的 RESTful 接口方法
 *
 * 使用方式：
 *   import { userApi, houseApi, orderApi } from '../../api'
 *   const res = await userApi.login({ username, password })
 *   // res 已经是 { code, message, data } 格式（拦截器已自动解包 axios 外层）
 *
 * 后端接口约定：
 *   - 所有接口统一返回格式：{ code: 200, message: "成功", data: {...} }
 *   - 分页接口：GET /xxx/page?current=1&size=10，返回 { records: [], total: N }
 */

import axios from 'axios'

/**
 * 创建 axios 实例，设置统一的前置配置
 * - baseURL: 所有请求以 /api 开头，开发时通过 Vite proxy 转发到后端
 * - timeout: 10 秒超时，超时后会触发请求错误
 * - headers: 默认请求体格式为 JSON
 */
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

/**
 * 响应拦截器
 * - 成功时：自动解包一层 response.data，调用方直接拿到后端返回的 JSON 对象
 * - 失败时：打印错误到控制台，并将 Promise 继续 reject 出去，由调用方 catch
 */
api.interceptors.response.use(r => r.data, err => { console.error(err); return Promise.reject(err) })

export default api

// ========== 用户相关 API ==========
export const userApi = {
  /** 登录：提交用户名和密码，返回用户信息（含 role 角色字段） */
  login: (params) => api.post('/user/login', params),
  /** 注册：提交用户名、密码、邮箱、验证码等注册信息 */
  register: (data) => api.post('/user/register', data),
  /** 发送邮箱验证码：后端向指定邮箱发送验证码 */
  sendCode: (email) => api.post('/user/send-code', { email }),
  /** 分页查询用户列表：支持按角色等条件筛选 */
  page: (params) => api.get('/user/page', { params }),
  /** 根据 ID 查询单个用户详情 */
  getById: (id) => api.get(`/user/${id}`),
  /** 新增用户（管理员后台创建用户） */
  save: (data) => api.post('/user', data),
  /** 更新用户信息 */
  update: (data) => api.put('/user', data),
  /** 根据 ID 删除用户 */
  delete: (id) => api.delete(`/user/${id}`),
}

// ========== 房源相关 API ==========
export const houseApi = {
  /** 分页查询房源列表：支持按标题、状态、房东 ID 等条件筛选 */
  page: (params) => api.get('/house/page', { params }),
  /** 根据 ID 查询单个房源详情 */
  getById: (id) => api.get(`/house/${id}`),
  /** 新增房源（房东发布房源 / 管理员创建） */
  save: (data) => api.post('/house', data),
  /** 更新房源信息 */
  update: (data) => api.put('/house', data),
  /** 根据 ID 删除房源 */
  delete: (id) => api.delete(`/house/${id}`),
  /** AI 智能推荐：提交用户偏好和需求描述，后端调用 DeepSeek 大模型进行分析匹配 */
  recommend: (data) => api.post('/house/recommend', data),
}

// ========== 订单相关 API ==========
export const orderApi = {
  /** 分页查询订单列表：支持按租客 ID、房东 ID 等条件筛选 */
  page: (params) => api.get('/order/page', { params }),
  /** 根据 ID 查询单个订单详情 */
  getById: (id) => api.get(`/order/${id}`),
  /** 创建新订单（租客下单） */
  save: (data) => api.post('/order', data),
  /** 更新订单（如房东确认/拒绝、标记退租等状态变更） */
  update: (data) => api.put('/order', data),
  /** 删除订单（管理员操作） */
  delete: (id) => api.delete(`/order/${id}`),
}
