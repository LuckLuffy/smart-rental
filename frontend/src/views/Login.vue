<!--
  Login.vue - 登录 / 注册页面

  作用：作为系统的入口页面，提供两种工作模式：
    1. 登录模式（mode === 'login'）：用户通过用户名和密码登录，
       成功后根据角色（TENANT / LANDLORD / ADMIN）跳转到对应首页。
    2. 注册模式（mode === 'register'）：新用户填写注册信息，
       通过邮箱验证码完成账号注册，注册成功后自动切换回登录模式。

  交互流程：
    登录 -> 调用 userApi.login -> 后端返回用户信息（含 role）-> 存入
    localStorage -> router.push 跳转到对应角色首页
    注册 -> 先发送邮箱验证码 -> 填写注册信息 -> 调用 userApi.register
    -> 成功后返回登录页

  数据存储：
    登录成功后，用户 JSON 信息存入 localStorage 的 'user' 键中，
    供路由守卫（router/index.js）和各个页面读取当前用户信息。
-->

<template>
  <div class="login-container">
    <div class="login-card">
      <h1>🏠 智慧租房管理系统</h1>

      <!-- ========== 登录模式 ========== -->
      <template v-if="mode === 'login'">
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" show-password @keyup.enter="handleLogin" />
          </el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loginLoading" @click="handleLogin">
            登 录
          </el-button>
        </el-form>
        <div class="switch-mode">
          还没有账号？<a href="javascript:void(0)" @click="switchToRegister">立即注册</a>
        </div>
      </template>

      <!-- ========== 注册模式 ========== -->
      <template v-if="mode === 'register'">
        <el-form ref="regFormRef" :model="regForm" :rules="regRules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="regForm.username" placeholder="请输入用户名" size="large" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="regForm.email" placeholder="请输入邮箱地址" size="large" />
          </el-form-item>
          <el-form-item label="验证码" prop="code">
            <div style="display:flex;gap:8px;">
              <el-input v-model="regForm.code" placeholder="请输入验证码" size="large" style="flex:1" />
              <el-button :disabled="codeCountdown > 0" @click="sendCode" size="large">
                {{ codeCountdown > 0 ? codeCountdown + 's' : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="regForm.password" type="password" placeholder="至少6位密码" size="large" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="regForm.confirmPassword" type="password" placeholder="请再次输入密码" size="large" show-password />
          </el-form-item>
          <el-button type="success" size="large" style="width:100%" :loading="regLoading" @click="handleRegister">
            注 册
          </el-button>
        </el-form>
        <div class="switch-mode">
          已有账号？<a href="javascript:void(0)" @click="switchToLogin">返回登录</a>
        </div>
      </template>

      <!-- 提示信息 -->
      <div class="tips">
        <div>💡 提示：</div>
        <div>· 注册需要有效的邮箱地址以接收验证码</div>
        <div>· 注册成功后账号类型默认为「租客」</div>
        <div>· 如需房东或管理员账号，请联系系统管理员</div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Login.vue - 登录/注册页面逻辑
 *
 * 核心逻辑：
 *   1. mode 响应式变量控制当前显示的是"登录"还是"注册"界面
 *   2. 登录成功后，用户信息存入 localStorage，路由跳转到角色对应的首页
 *   3. 注册时需要先通过邮箱获取验证码，再提交完整的注册信息
 */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const mode = ref('login')               // 'login' | 'register'，控制界面切换

// ========== 登录相关逻辑 ==========
const loginFormRef = ref()               // 登录表单的引用，用于表单校验
const loginLoading = ref(false)          // 登录按钮的加载状态
const loginForm = reactive({ username: '', password: '' })  // 登录表单数据模型
// 登录表单的校验规则：用户名和密码均为必填
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
// 角色到路由前缀的映射表，用于登录成功后根据角色跳转
const roleMap = { 'TENANT': '/tenant', 'LANDLORD': '/landlord', 'ADMIN': '/admin' }

/**
 * 处理登录逻辑
 * 1. 对表单进行校验，不通过则直接返回
 * 2. 调用 userApi.login 向后端发送登录请求
 * 3. 成功后：将用户数据序列化存入 localStorage（供路由守卫和其他页面读取）
 *            弹出成功提示并按角色跳转到对应首页
 * 4. 失败后：显示后端返回的错误消息或默认提示
 */
const handleLogin = async () => {
  // 触发 Element Plus 表单校验，校验不通过则停止执行
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
  loginLoading.value = true
  try {
    const res = await userApi.login(loginForm)
    if (res.code === 200) {
      // 将用户信息持久化到 localStorage，后续页面通过读取该值获取当前用户
      localStorage.setItem('user', JSON.stringify(res.data))
      ElMessage.success('登录成功')
      // 根据角色跳转到对应的首页
      router.push(roleMap[res.data.role] || '/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch { ElMessage.error('登录失败') }
  finally { loginLoading.value = false }
}

// ========== 注册相关逻辑 ==========
const regFormRef = ref()                 // 注册表单的引用，用于表单校验
const regLoading = ref(false)            // 注册按钮的加载状态
const regForm = reactive({ username: '', password: '', confirmPassword: '', email: '', code: '' })
const codeCountdown = ref(0)             // 验证码发送后 60 秒倒计时

/**
 * 自定义校验器：校验确认密码是否与密码一致
 */
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== regForm.password) callback(new Error('两次密码输入不一致'))
  else callback()
}

// 注册表单校验规则集
const regRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

/**
 * 发送邮箱验证码
 * 1. 先对邮箱格式做简单的前端校验
 * 2. 调用 userApi.sendCode 让后端发送邮件
 * 3. 成功后启动 60 秒倒计时，期间禁止重复点击，按钮显示剩余秒数
 */
const sendCode = async () => {
  // 前置校验：邮箱不能为空
  if (!regForm.email) { ElMessage.warning('请先输入邮箱'); return }
  // 前置校验：邮箱格式必须合法
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(regForm.email)) { ElMessage.warning('请输入正确的邮箱格式'); return }

  try {
    const res = await userApi.sendCode(regForm.email)
    if (res.code === 200) {
      ElMessage.success('验证码已发送，请查收邮件')
      // 启动 60 秒倒计时，控制按钮的 disabled 状态
      codeCountdown.value = 60
      const timer = setInterval(() => {
        codeCountdown.value--
        if (codeCountdown.value <= 0) clearInterval(timer)
      }, 1000)
    } else {
      ElMessage.error(res.message || '发送失败')
    }
  } catch { ElMessage.error('发送失败，请稍后重试') }
}

/**
 * 处理注册逻辑
 * 1. 对注册表单进行校验
 * 2. 调用 userApi.register 提交注册信息到后端
 * 3. 成功后清空表单并切换回登录模式
 */
const handleRegister = async () => {
  const valid = await regFormRef.value.validate().catch(() => false)
  if (!valid) return
  regLoading.value = true
  try {
    const res = await userApi.register({
      username: regForm.username,
      password: regForm.password,
      realName: regForm.username,         // 注册时默认使用用户名作为真实姓名
      email: regForm.email,
      code: regForm.code
    })
    if (res.code === 200) {
      ElMessage.success('注册成功！即将跳转到登录...')
      // 清空注册表单中的所有字段
      Object.assign(regForm, { username: '', password: '', confirmPassword: '', email: '', code: '' })
      switchToLogin()                     // 自动切换回登录界面
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch { ElMessage.error('注册失败') }
  finally { regLoading.value = false }
}

// ========== 界面切换 ==========
/** 切换到注册模式 */
const switchToRegister = () => { mode.value = 'register' }
/** 切换到登录模式 */
const switchToLogin = () => { mode.value = 'login' }
</script>

<style scoped>
.login-container { height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.login-card { width: 420px; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); }
h1 { text-align: center; font-size: 22px; color: #303133; margin-bottom: 30px; }
.tips { margin-top: 20px; padding: 12px; background: #f5f7fa; border-radius: 8px; font-size: 12px; color: #909399; line-height: 1.8; }
.switch-mode { margin-top: 16px; text-align: center; font-size: 13px; color: #909399; }
.switch-mode a { color: #409eff; text-decoration: none; }
.switch-mode a:hover { text-decoration: underline; }
</style>
