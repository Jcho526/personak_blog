<template>
  <div class="site-shell auth-page register-page">
    <SiteHeader />

    <main class="page-main auth-main">
      <div class="container auth-container">
        <section class="auth-card surface-card">
          <h2 class="auth-title">创建账号</h2>
          <p class="auth-subtitle">只需一步，开始搭建你的个人知识空间</p>
          <form class="auth-form" @submit.prevent="register">
            <div class="form-group">
              <label for="username" class="form-label">用户名</label>
              <input type="text" id="username" v-model="registerForm.username" class="form-control" placeholder="请输入用户名" required>
            </div>
            <div class="form-group">
              <label for="email" class="form-label">邮箱</label>
              <input type="email" id="email" v-model="registerForm.email" class="form-control" placeholder="请输入邮箱" required>
            </div>
            <div class="form-group">
              <label for="password" class="form-label">密码</label>
              <input type="password" id="password" v-model="registerForm.password" class="form-control" placeholder="请输入密码" required>
            </div>
            <div class="form-group">
              <label for="confirmPassword" class="form-label">确认密码</label>
              <input type="password" id="confirmPassword" v-model="registerForm.confirmPassword" class="form-control" placeholder="请确认密码" required>
            </div>
            <div class="form-actions">
              <button type="submit" class="btn btn-primary" :disabled="loading">
                {{ loading ? '注册中...' : '注册' }}
              </button>
              <router-link to="/login" class="auth-link">已有账号？去登录</router-link>
            </div>
          </form>
        </section>
      </div>
    </main>

    <footer class="site-footer">
      <div class="container">
        <p class="footer-text">© 2026 个人博客</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { ElMessage } from 'element-plus'
import SiteHeader from '../components/SiteHeader.vue'

const router = useRouter()
const loading = ref(false)
const registerForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const register = async () => {
  if (loading.value) return

  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    const res = await userApi.register(registerForm.value)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (error) {
    console.error('Register failed:', error)
    if (error.response && error.response.data) {
       ElMessage.error(error.response.data.message || '注册失败，请检查输入信息')
    } else {
       ElMessage.error('注册失败，请检查网络或联系管理员')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.auth-main {
  display: grid;
  place-items: center;
}

.auth-container {
  width: 100%;
  max-width: 560px;
}

.auth-card {
  padding: 30px;
  background: linear-gradient(180deg, #ffffff, #fbfcff);
}

.auth-title {
  margin: 0;
  font-size: 28px;
  text-align: center;
}

.auth-subtitle {
  margin: 8px 0 22px;
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
}

.auth-form {
  display: grid;
  gap: 14px;
}

.form-group {
  display: grid;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
}

.form-actions {
  margin-top: 2px;
  display: grid;
  gap: 12px;
}

.auth-link {
  text-align: center;
  font-size: 14px;
}

@media (max-width: 768px) {
  .auth-card {
    padding: 22px 18px;
  }

  .auth-title {
    font-size: 24px;
  }
}
</style>
