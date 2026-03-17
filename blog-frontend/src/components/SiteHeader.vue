<template>
  <header class="site-header">
    <div class="container header-inner">
      <router-link to="/" class="site-title-link">
        <h1 class="site-title">个人博客</h1>
      </router-link>
      <nav class="site-nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/about" class="nav-link">关于</router-link>
        
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-avatar-wrapper">
              <el-avatar :size="36" :src="userAvatar" class="user-avatar">
                {{ userInitial }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="write">
                  <el-icon><EditPen /></el-icon>
                  写文章
                </el-dropdown-item>
                <el-dropdown-item command="myArticles">
                  <el-icon><Document /></el-icon>
                  我的文章
                </el-dropdown-item>
                <el-dropdown-item command="myFavorites">
                  <el-icon><Star /></el-icon>
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="myLikes">
                  <el-icon><Pointer /></el-icon>
                  我的点赞
                </el-dropdown-item>
                <el-dropdown-item divided command="profile">
                  <el-icon><User /></el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        
        <template v-else>
          <router-link to="/login" class="nav-link">登录</router-link>
          <router-link to="/register" class="nav-link">注册</router-link>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Lock, SwitchButton, EditPen, Document, Star, Pointer } from '@element-plus/icons-vue'
import { userApi } from '../api'

const router = useRouter()
const userStore = useUserStore()

const API_BASE_URL = 'http://localhost:8080'

const userAvatar = computed(() => {
  const avatar = userStore.user?.avatar
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return API_BASE_URL + avatar
})

const userInitial = computed(() => {
  const username = userStore.user?.username || ''
  return username ? username.charAt(0).toUpperCase() : 'U'
})

const handleCommand = (command) => {
  switch (command) {
    case 'write':
      router.push('/write')
      break
    case 'myArticles':
      router.push('/my-articles')
      break
    case 'myFavorites':
      router.push('/my-favorites')
      break
    case 'myLikes':
      router.push('/my-likes')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      router.push('/change-password')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/')
      }).catch(() => {})
      break
  }
}

onMounted(async () => {
  if (userStore.isLoggedIn && !userStore.user) {
    try {
      const res = await userApi.getUserInfo()
      if (res.code === 200) {
        userStore.setUser(res.data)
      }
    } catch (error) {
      console.error('Failed to get user info:', error)
    }
  }
})
</script>

<style scoped lang="scss">
.site-title-link {
  text-decoration: none;
}

.user-avatar-wrapper {
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  transition: background-color 0.2s ease;
  
  &:hover {
    background: rgba(91, 92, 240, 0.1);
  }
}

.user-avatar {
  background: var(--gradient-main);
  color: #fff;
  font-weight: 600;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .el-icon {
    font-size: 16px;
  }
}
</style>
