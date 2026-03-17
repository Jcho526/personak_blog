<template>
  <div class="site-shell my-likes-page">
    <SiteHeader />
    
    <main class="page-main">
      <div class="container">
        <div class="page-header">
          <h2 class="page-title">我的点赞</h2>
          <span class="count-badge">{{ articles.length }} 篇文章</span>
        </div>
        
        <div class="articles-list">
          <article v-for="article in articles" :key="article.id" class="article-card surface-card" @click="goToArticle(article.id)">
            <h3 class="article-title">{{ article.title }}</h3>
            <div class="article-meta">
              <span>{{ formatDate(article.createdAt) }}</span>
              <span>{{ article.categoryName || '未分类' }}</span>
              <span>{{ article.viewCount }} 浏览</span>
            </div>
            <p class="article-excerpt">{{ article.excerpt }}</p>
            <div class="article-tags">
              <span v-for="tag in article.tags || []" :key="tag.id" class="tag-pill">
                # {{ tag.name }}
              </span>
            </div>
          </article>
          
          <div v-if="!articles.length" class="empty-state">
            <el-icon :size="48" color="#94a3b8"><Pointer /></el-icon>
            <p>还没有点赞过文章</p>
            <router-link to="/">
              <el-button type="primary">去发现好文章</el-button>
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
import { Pointer } from '@element-plus/icons-vue'
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
    const res = await articleApi.getMyLikes()
    if (res.code === 200) {
      articles.value = res.data
    }
  } catch (error) {
    console.error('Failed to load likes:', error)
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

.count-badge {
  background: rgba(236, 72, 153, 0.1);
  color: #ec4899;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 14px;
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-card {
  padding: 24px;
  transition: transform 0.2s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-2px);
  }
}

.article-title {
  margin: 0 0 10px;
  font-size: 20px;
  color: var(--text);
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 13px;
  color: var(--text-secondary);
}

.article-excerpt {
  margin: 0 0 12px;
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.6;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-secondary);
  
  p {
    margin: 20px 0;
    font-size: 16px;
  }
}
</style>
