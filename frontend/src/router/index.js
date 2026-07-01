/**
 * 智慧租房管理系统 - 路由配置模块
 *
 * 作用：
 *   1. 定义三个角色端（租客/房东/管理员）的页面路由结构
 *   2. 使用路由懒加载（() => import(...)）优化首屏加载速度
 *   3. 通过全局前置导航守卫（beforeEach）实现登录态校验和角色权限控制
 *
 * 路由层级说明：
 *   /login          —— 登录/注册页（公开）
 *   /tenant/*       —— 租客端（需 TENANT 角色，嵌套子路由）
 *   /landlord/*     —— 房东端（需 LANDLORD 角色，嵌套子路由）
 *   /admin/*        —— 管理端（需 ADMIN 角色，嵌套子路由）
 */

import { createRouter, createWebHistory } from 'vue-router'

// ========== 路由表定义 ==========
// 每个路由的 meta.role 字段标记了该页面允许访问的角色，
// 导航守卫会据此校验当前用户的角色是否匹配。
const routes = [
  // 访问根路径自动跳转到登录页
  { path: '/', redirect: '/login' },
  // 登录/注册页（公开页面，无需登录即可访问）
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },

  // ---------- 租客端路由（嵌套在 TenantLayout 中） ----------
  {
    path: '/tenant',
    component: () => import('../views/tenant/TenantLayout.vue'),
    meta: { role: 'TENANT' },           // 标记：仅 TENANT 角色可访问
    children: [
      { path: '', redirect: '/tenant/dashboard' },
      { path: 'dashboard', name: 'TenantDashboard', component: () => import('../views/tenant/TenantDashboard.vue') },
      { path: 'houses', name: 'TenantHouses', component: () => import('../views/tenant/HouseList.vue') },
      { path: 'orders', name: 'TenantOrders', component: () => import('../views/tenant/MyOrders.vue') },
      { path: 'guidance', name: 'TenantGuidance', component: () => import('../views/tenant/RentalGuide.vue') },
    ]
  },

  // ---------- 房东端路由（嵌套在 LandlordLayout 中） ----------
  {
    path: '/landlord',
    component: () => import('../views/landlord/LandlordLayout.vue'),
    meta: { role: 'LANDLORD' },         // 标记：仅 LANDLORD 角色可访问
    children: [
      { path: '', redirect: '/landlord/dashboard' },
      { path: 'dashboard', name: 'LandlordDashboard', component: () => import('../views/landlord/LandlordDashboard.vue') },
      { path: 'houses', name: 'LandlordHouses', component: () => import('../views/landlord/MyHouses.vue') },
      { path: 'orders', name: 'LandlordOrders', component: () => import('../views/landlord/OrdersManage.vue') },
    ]
  },

  // ---------- 管理端路由（嵌套在 AdminLayout 中） ----------
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { role: 'ADMIN' },            // 标记：仅 ADMIN 角色可访问
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/AdminDashboard.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('../views/admin/UserList.vue') },
      { path: 'houses', name: 'AdminHouses', component: () => import('../views/admin/HouseList.vue') },
      { path: 'orders', name: 'AdminOrders', component: () => import('../views/admin/OrderList.vue') },
    ]
  },
]

// 创建路由实例（HTML5 History 模式，URL 中不带 #）
const router = createRouter({ history: createWebHistory(), routes })

// ========== 全局前置导航守卫（登录校验 + 角色权限控制） ==========
// 逻辑说明：
//   1. 访问登录页 -> 直接放行
//   2. 从 localStorage 读取已登录用户信息
//   3. 未登录 -> 强制跳转到登录页
//   4. 已登录但角色不匹配（如租客访问房东页面）-> 跳转到角色对应的首页
router.beforeEach((to, from, next) => {
  // 登录页对所有人开放，直接放行
  if (to.path === '/login') { next(); return }

  // 尝试从 localStorage 中恢复已登录的用户信息
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  // 未登录 -> 跳回登录页
  if (!user) { next('/login'); return }

  // 已登录但目标路由要求特定角色且与当前用户角色不符 -> 重定向到该角色的首页
  if (to.meta.role && to.meta.role !== user.role) {
    const roleMap = { 'TENANT': '/tenant', 'LANDLORD': '/landlord', 'ADMIN': '/admin' }
    next(roleMap[user.role] || '/login'); return
  }

  // 全部校验通过，放行
  next()
})

export default router
