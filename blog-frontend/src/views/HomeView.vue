<template>
  <div class="site-shell home-page">
    <SiteHeader />

    <main class="page-main home-main">
      <div class="container">
        <div class="home-layout">
          <section class="content">
            <article v-for="article in articles" :key="article.id" class="article-card surface-card" @click="goToArticle(article.id)">
              <h2 class="article-title">{{ article.title }}</h2>
              <div class="article-meta">
                <span>{{ formatDate(article.createdAt) }}</span>
                <span>{{ article.categoryName }}</span>
                <span>{{ article.viewCount }} 浏览</span>
              </div>
              <p class="article-excerpt">{{ article.excerpt }}</p>
              <div class="article-tags" @click.stop>
                <span v-for="tag in article.tags || []" :key="tag.id" class="tag-pill">
                  <router-link :to="`/tag/${tag.id}`"># {{ tag.name }}</router-link>
                </span>
              </div>
            </article>

            <div v-if="!articles.length" class="empty-state">暂无文章，稍后再来看看吧～</div>
          </section>

          <aside class="sidebar">
            <section class="sidebar-card surface-card">
              <h3 class="sidebar-title">分类</h3>
              <ul class="category-list">
                <li v-for="category in categories" :key="category.id" class="category-item">
                  <router-link :to="`/category/${category.id}`">
                    <span>{{ category.name }}</span>
                    <strong>{{ category.articleCount }}</strong>
                  </router-link>
                </li>
              </ul>
            </section>

            <section class="sidebar-card surface-card">
              <h3 class="sidebar-title">标签</h3>
              <div class="tag-cloud">
                <span v-for="tag in tags" :key="tag.id" class="tag-pill">
                  <router-link :to="`/tag/${tag.id}`">{{ tag.name }} ({{ tag.articleCount }})</router-link>
                </span>
              </div>
            </section>
          </aside>
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
import { articleApi, categoryApi, tagApi } from '../api'
import SiteHeader from '../components/SiteHeader.vue'

const router = useRouter()
const articles = ref([])
const categories = ref([])
const tags = ref([])

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const goToArticle = (id) => {
  router.push(`/article/${id}`)
}

onMounted(async () => {
  try {
    const [articlesRes, categoriesRes, tagsRes] = await Promise.all([
      articleApi.getArticles(),
      categoryApi.getCategories(),
      tagApi.getTags()
    ])

    if (articlesRes.code === 200) articles.value = articlesRes.data
    if (categoriesRes.code === 200) categories.value = categoriesRes.data
    if (tagsRes.code === 200) tags.value = tagsRes.data
  } catch (error) {
    console.error('Failed to load data:', error)
  }
})
</script>

<style scoped lang="scss">
.home-main {
  .home-layout {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 300px;
    gap: 24px;
  }

  .content {
    min-width: 0;
  }

  .article-card {
    padding: 24px;
    margin-bottom: 18px;
    transition: transform 0.25s ease;
    cursor: pointer;

    &:hover {
      transform: translateY(-3px);
    }
  }

  .article-title {
    margin: 0 0 10px;
    font-size: 22px;
    line-height: 1.35;
    color: var(--text);
  }

  .article-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px 14px;
    margin-bottom: 12px;
    font-size: 13px;
    color: var(--text-secondary);
  }

  .article-excerpt {
    margin: 0;
    color: #475569;
    font-size: 15px;
    line-height: 1.8;
  }

  .article-tags {
    margin-top: 14px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .sidebar {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .sidebar-card {
    padding: 18px;
  }

  .sidebar-title {
    margin: 0 0 14px;
    padding-bottom: 10px;
    font-size: 16px;
    border-bottom: 1px solid var(--border);
  }

  .category-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .category-item a {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    color: var(--text-secondary);
  }

  .category-item a:hover {
    color: var(--primary);
  }

  .tag-cloud {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
}

@media (max-width: 992px) {
  .home-main .home-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .home-main .article-card {
    padding: 18px;
  }

  .home-main .article-title {
    font-size: 19px;
  }
}
</style>
