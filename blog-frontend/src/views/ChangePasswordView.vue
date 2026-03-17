<template>
  <div class="site-shell change-password-page">
    <SiteHeader />
    
    <main class="page-main">
      <div class="container">
        <div class="password-container">
          <section class="password-card surface-card">
            <h2 class="password-title">修改密码</h2>
            <p class="password-subtitle">请输入当前密码和新密码</p>
            
            <el-form
              ref="formRef"
              :model="passwordForm"
              :rules="rules"
              label-width="100px"
              class="password-form"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleSubmit" :loading="loading">
                  确认修改
                </el-button>
                <el-button @click="handleCancel">取消</el-button>
              </el-form-item>
            </el-form>
          </section>
        </div>
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { ElMessage } from 'element-plus'
import SiteHeader from '../components/SiteHeader.vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await userApi.changePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        if (res.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          router.push('/login')
        } else {
          ElMessage.error(res.message || '修改失败')
        }
      } catch (error) {
        if (error.response?.data?.message) {
          ElMessage.error(error.response.data.message)
        } else {
          ElMessage.error('修改失败，请稍后重试')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.password-container {
  max-width: 500px;
  margin: 0 auto;
}

.password-card {
  padding: 32px;
}

.password-title {
  margin: 0;
  font-size: 24px;
  text-align: center;
  color: var(--text);
}

.password-subtitle {
  margin: 8px 0 24px;
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
}

.password-form {
  padding-top: 16px;
}

@media (max-width: 768px) {
  .password-card {
    padding: 24px 16px;
  }
  
  .password-title {
    font-size: 20px;
  }
}
</style>
