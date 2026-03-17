<template>
  <div class="site-shell auth-page login-page">
    <SiteHeader />

    <main class="page-main auth-main">
      <div class="container auth-container">
        <section class="auth-card surface-card">
          <h2 class="auth-title">欢迎回来</h2>
          <p class="auth-subtitle">登录后继续阅读与分享你的知识笔记</p>
          <form class="auth-form" @submit.prevent="login">
            <div class="form-group">
              <label for="username" class="form-label">用户名</label>
              <input type="text" id="username" v-model="loginForm.username" class="form-control" placeholder="请输入用户名" required>
            </div>
            <div class="form-group">
              <label for="password" class="form-label">密码</label>
              <input type="password" id="password" v-model="loginForm.password" class="form-control" placeholder="请输入密码" required>
            </div>
            <div class="form-actions">
              <button type="submit" class="btn btn-primary" :disabled="loading">
                {{ loading ? '登录中...' : '登录' }}
              </button>
              <router-link to="/register" class="auth-link">没有账号？去注册</router-link>
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
import { useUserStore } from '../store'
import { ElMessage } from 'element-plus'
import SiteHeader from '../components/SiteHeader.vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const loginForm = ref({
  username: '',
  password: ''
})

const login = async () => {
  if (loading.value) return
  
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const res = await userApi.login(loginForm.value)
    if (res.code === 200) {
      userStore.setToken(res.data.token)
      userStore.setUser(res.data)
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    console.error('Login failed:', error)
    if (error.response && error.response.data) {
       ElMessage.error(error.response.data.message || '登录失败，请检查用户名和密码')
    } else {
       ElMessage.error('登录失败，请检查网络或联系管理员')
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
