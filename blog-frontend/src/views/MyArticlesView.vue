<template>
  <div class="site-shell my-articles-page">
    <SiteHeader />
    
    <main class="page-main">
      <div class="container">
        <div class="page-header">
          <h2 class="page-title">我的文章</h2>
          <router-link to="/write">
            <el-button type="primary" size="small">
              <el-icon><Plus /></el-icon>
              写文章
            </el-button>
          </router-link>
        </div>
        
        <div class="articles-list">
          <article v-for="article in articles" :key="article.id" class="article-card surface-card" @click="goToArticle(article.id)">
            <div class="article-content">
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-excerpt">{{ article.excerpt }}</p>
              <div class="article-meta">
                <span class="status-badge" :class="article.status">{{ article.status === 'published' ? '已发布' : '草稿' }}</span>
                <span>{{ formatDate(article.createdAt) }}</span>
                <span>{{ article.categoryName || '未分类' }}</span>
                <span>{{ article.viewCount }} 浏览</span>
              </div>
            </div>
            <div class="article-actions" @click.stop>
              <router-link :to="`/edit/${article.id}`" class="action-btn edit">
                <el-icon><Edit /></el-icon>
                编辑
              </router-link>
              <button class="action-btn delete" @click="handleDelete(article.id)">
                <el-icon><Delete /></el-icon>
                删除
              </button>
            </div>
          </article>
          
          <div v-if="!articles.length" class="empty-state">
            <p>还没有写过文章</p>
            <router-link to="/write">
              <el-button type="primary">
                <el-icon><Plus /></el-icon>
                开始写作
              </el-button>
            </router-link>
          </div>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { articleApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, Plus } from '@element-plus/icons-vue'
import SiteHeader from '../components/SiteHeader.vue'

const router = useRouter()
const userStore = useUserStore()
const articles = ref([])

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

const goToArticle = (id) => {
  router.push(`/article/${id}`)
}

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loadArticles()
})

const loadArticles = async () => {
  try {
    const res = await articleApi.getMyArticles()
    if (res.code === 200) {
      articles.value = res.data
    }
  } catch (error) {
    console.error('Failed to load articles:', error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await articleApi.deleteArticle(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      articles.value = articles.value.filter(a => a.id !== id)
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
  font-size: 28px;
  color: var(--text);
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-card {
  display: flex;
  justify-content: space-between;
  padding: 24px;
  transition: transform 0.2s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-2px);
  }
}

.article-content {
  flex: 1;
  min-width: 0;
}

.article-title {
  margin: 0 0 8px;
  font-size: 20px;
  color: var(--text);
}

.article-excerpt {
  margin: 0 0 12px;
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: var(--text-secondary);
}

.status-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  
  &.published {
    background: rgba(22, 163, 74, 0.1);
    color: #16a34a;
  }
  
  &.draft {
    background: rgba(245, 158, 11, 0.1);
    color: #f59e0b;
  }
}

.article-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 20px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
  
  &.edit {
    background: rgba(91, 92, 240, 0.1);
    color: var(--primary);
    
    &:hover {
      background: rgba(91, 92, 240, 0.2);
    }
  }
  
  &.delete {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
    
    &:hover {
      background: rgba(239, 68, 68, 0.2);
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
  
  p {
    margin-bottom: 20px;
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .article-card {
    flex-direction: column;
    padding: 18px;
  }
  
  .article-actions {
    flex-direction: row;
    margin-left: 0;
    margin-top: 16px;
  }
  
  .action-btn {
    flex: 1;
    justify-content: center;
  }
}
</style>
