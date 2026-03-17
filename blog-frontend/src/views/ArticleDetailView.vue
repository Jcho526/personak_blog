<template>
  <div class="site-shell detail-page">
    <SiteHeader />

    <main class="page-main detail-main">
      <div class="container">
        <article class="article surface-card" v-if="article">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span>{{ formatDate(article.createdAt) }}</span>
            <span>{{ article.categoryName }}</span>
            <span>{{ article.viewCount }} 浏览</span>
          </div>
          <div class="article-content" v-html="renderMarkdown(article.content)"></div>
          <div class="article-tags">
            <span v-for="tag in article.tags || []" :key="tag.id" class="tag-pill">
              <router-link :to="`/tag/${tag.id}`"># {{ tag.name }}</router-link>
            </span>
          </div>
          <div class="article-actions">
            <el-button
              :type="isLiked ? 'primary' : 'default'"
              :icon="isLiked ? StarFilled : Star"
              @click="handleLike"
              :loading="likeLoading"
            >
              {{ isLiked ? '已点赞' : '点赞' }}
            </el-button>
            <el-button
              :type="isFavorited ? 'warning' : 'default'"
              :icon="isFavorited ? CollectionTag : Collection"
              @click="handleFavorite"
              :loading="favoriteLoading"
            >
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </article>
        <div v-else class="empty-state">文章加载中或不存在</div>
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
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled, Collection, CollectionTag } from '@element-plus/icons-vue'
import { articleApi } from '../api'
import { useUserStore } from '../store'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import SiteHeader from '../components/SiteHeader.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const article = ref(null)
const isLiked = ref(false)
const isFavorited = ref(false)
const likeLoading = ref(false)
const favoriteLoading = ref(false)

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch (__) {}
    }
    return ''
  }
})

const renderMarkdown = (content) => {
  if (!content) return ''
  return md.render(content)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  likeLoading.value = true
  try {
    if (isLiked.value) {
      const res = await articleApi.unlikeArticle(route.params.id)
      if (res.code === 200) {
        isLiked.value = false
        ElMessage.success('已取消点赞')
      }
    } else {
      const res = await articleApi.likeArticle(route.params.id)
      if (res.code === 200) {
        isLiked.value = true
        ElMessage.success('点赞成功')
      }
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    likeLoading.value = false
  }
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  favoriteLoading.value = true
  try {
    if (isFavorited.value) {
      const res = await articleApi.unfavoriteArticle(route.params.id)
      if (res.code === 200) {
        isFavorited.value = false
        ElMessage.success('已取消收藏')
      }
    } else {
      const res = await articleApi.favoriteArticle(route.params.id)
      if (res.code === 200) {
        isFavorited.value = true
        ElMessage.success('收藏成功')
      }
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await articleApi.getArticleById(route.params.id)
    if (res.code === 200) {
      article.value = res.data.article || res.data
      if (res.data.isLiked !== undefined) {
        isLiked.value = res.data.isLiked
      }
      if (res.data.isFavorited !== undefined) {
        isFavorited.value = res.data.isFavorited
      }
    }
  } catch (error) {
    console.error('Failed to load article:', error)
  }
})
</script>

<style scoped lang="scss">
.detail-main {
  .container {
    max-width: 860px;
  }

  .article {
    padding: 30px;
  }

  .article-title {
    margin: 0 0 14px;
    font-size: 34px;
    line-height: 1.25;
    letter-spacing: -0.4px;
  }

  .article-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px 14px;
    margin-bottom: 24px;
    color: var(--text-secondary);
    font-size: 13px;
  }

  .article-content {
    color: #334155;
    font-size: 16px;
    line-height: 1.9;
    margin-bottom: 24px;

    :deep(h1),
    :deep(h2),
    :deep(h3),
    :deep(h4) {
      color: var(--text);
      margin-top: 1.6em;
      margin-bottom: 0.6em;
      line-height: 1.35;
    }

    :deep(p),
    :deep(ul),
    :deep(ol),
    :deep(blockquote) {
      margin: 1em 0;
    }

    :deep(blockquote) {
      margin-left: 0;
      padding: 10px 14px;
      border-left: 4px solid #818cf8;
      background: #eef2ff;
      border-radius: 8px;
      color: #4338ca;
    }

    :deep(img) {
      max-width: 100%;
      border-radius: 12px;
      border: 1px solid var(--border);
    }

    :deep(pre) {
      background: #0f172a;
      color: #e2e8f0;
      padding: 16px;
      border-radius: 12px;
      overflow: auto;
    }

    :deep(code) {
      font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
      font-size: 0.9em;
    }

    :deep(:not(pre) > code) {
      padding: 2px 6px;
      border-radius: 6px;
      background: #eef2ff;
      color: #4338ca;
    }
  }

  .article-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 24px;
  }

  .article-actions {
    display: flex;
    gap: 12px;
    padding-top: 20px;
    border-top: 1px solid var(--border);
  }
}

@media (max-width: 768px) {
  .detail-main .article {
    padding: 18px;
  }

  .detail-main .article-title {
    font-size: 26px;
  }
}
</style>
