<template>
  <div class="site-shell profile-page">
    <SiteHeader />
    
    <main class="page-main">
      <div class="container">
        <div class="profile-container">
          <section class="profile-card surface-card">
            <div class="profile-header">
              <div class="avatar-section">
                <el-avatar :size="100" :src="userAvatarUrl" class="profile-avatar">
                  {{ userInitial }}
                </el-avatar>
                <el-upload
                  class="avatar-upload"
                  action="#"
                  :show-file-list="false"
                  :auto-upload="true"
                  :before-upload="beforeAvatarUpload"
                  :on-success="handleAvatarSuccess"
                  :on-error="handleAvatarError"
                  :http-request="handleAvatarUpload"
                >
                  <el-button size="small" type="primary">更换头像</el-button>
                </el-upload>
              </div>
              <h2 class="profile-username">{{ userStore.user?.username }}</h2>
              <p class="profile-email">{{ userStore.user?.email }}</p>
            </div>
            
            <el-divider />
            
            <el-form
              ref="formRef"
              :model="profileForm"
              :rules="rules"
              label-width="80px"
              class="profile-form"
            >
              <el-form-item label="用户名" prop="username">
                <el-input v-model="profileForm.username" placeholder="请输入用户名" />
              </el-form-item>
              
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              
              <el-form-item label="个人简介" prop="bio">
                <el-input
                  v-model="profileForm.bio"
                  type="textarea"
                  :rows="4"
                  placeholder="介绍一下自己吧..."
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleSubmit" :loading="loading">
                  保存修改
                </el-button>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '../store'
import { userApi } from '../api'
import { ElMessage } from 'element-plus'
import SiteHeader from '../components/SiteHeader.vue'

const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const API_BASE_URL = 'http://localhost:8080'

const userInitial = computed(() => {
  const username = userStore.user?.username || ''
  return username ? username.charAt(0).toUpperCase() : 'U'
})

const userAvatarUrl = computed(() => {
  const avatar = userStore.user?.avatar
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return API_BASE_URL + avatar
})

const profileForm = reactive({
  username: '',
  email: '',
  bio: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

onMounted(() => {
  if (userStore.user) {
    profileForm.username = userStore.user.username || ''
    profileForm.email = userStore.user.email || ''
    profileForm.bio = userStore.user.bio || ''
  }
})

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  
  try {
    const res = await userApi.uploadAvatar(formData)
    if (res.code === 200) {
      options.onSuccess(res)
    } else {
      options.onError(new Error(res.message || '上传失败'))
    }
  } catch (error) {
    console.error('Upload error:', error)
    const message = error.response?.data?.message || error.message || '上传失败'
    options.onError(new Error(message))
  }
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    userStore.setUser({ ...userStore.user, avatar: response.data })
    ElMessage.success('头像上传成功')
  }
}

const handleAvatarError = (error) => {
  ElMessage.error(error?.message || '头像上传失败')
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await userApi.updateProfile(profileForm)
        if (res.code === 200) {
          userStore.setUser({ ...userStore.user, ...profileForm })
          ElMessage.success('个人信息更新成功')
        } else {
          ElMessage.error(res.message || '更新失败')
        }
      } catch (error) {
        ElMessage.error('更新失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.profile-container {
  max-width: 600px;
  margin: 0 auto;
}

.profile-card {
  padding: 32px;
}

.profile-header {
  text-align: center;
  padding-bottom: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.profile-avatar {
  background: var(--gradient-main);
  color: #fff;
  font-size: 36px;
  font-weight: 600;
}

.profile-username {
  margin: 0;
  font-size: 24px;
  color: var(--text);
}

.profile-email {
  margin: 8px 0 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.profile-form {
  padding-top: 20px;
}

.avatar-upload {
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .profile-card {
    padding: 24px 16px;
  }
  
  .profile-username {
    font-size: 20px;
  }
}
</style>
