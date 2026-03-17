<template>
  <div class="site-shell category-page">
    <SiteHeader />

    <main class="page-main list-main">
      <div class="container">
        <h2 class="page-title">分类：{{ categoryName }}</h2>
        <div class="list-layout">
          <section class="content">
            <article v-for="article in articles" :key="article.id" class="article-card surface-card">
              <h3 class="article-title">
                <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
              </h3>
              <div class="article-meta">
                <span>{{ formatDate(article.createdAt) }}</span>
                <span>{{ article.categoryName }}</span>
                <span>{{ article.viewCount }} 浏览</span>
              </div>
              <p class="article-excerpt">{{ article.excerpt }}</p>
              <div class="article-tags">
                <span v-for="tag in article.tags || []" :key="tag.id" class="tag-pill">
                  <router-link :to="`/tag/${tag.id}`"># {{ tag.name }}</router-link>
                </span>
              </div>
            </article>

            <div v-if="!articles.length" class="empty-state">该分类下暂无文章</div>
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
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi, categoryApi, tagApi } from '../api'
import SiteHeader from '../components/SiteHeader.vue'

const route = useRoute()
const categoryName = ref('分类')
const articles = ref([])
const categories = ref([])
const tags = ref([])

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const loadData = async () => {
  try {
    const [articlesRes, categoriesRes, tagsRes] = await Promise.all([
      articleApi.getArticlesByCategory(route.params.id),
      categoryApi.getCategories(),
      tagApi.getTags()
    ])

    articles.value = articlesRes.code === 200 ? articlesRes.data : []
    categories.value = categoriesRes.code === 200 ? categoriesRes.data : []
    tags.value = tagsRes.code === 200 ? tagsRes.data : []
    categoryName.value = categories.value.find((item) => String(item.id) === String(route.params.id))?.name || '分类'
  } catch (error) {
    console.error('Failed to load category data:', error)
  }
}

onMounted(loadData)
watch(() => route.params.id, loadData)
</script>

<style scoped lang="scss">
.list-main {
  .page-title {
    margin: 0 0 18px;
    font-size: 26px;
  }

  .list-layout {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 300px;
    gap: 24px;
  }

  .article-card {
    padding: 24px;
    margin-bottom: 18px;
  }

  .article-title {
    margin: 0 0 10px;
    font-size: 21px;
  }

  .article-title a {
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
    line-height: 1.8;
  }

  .article-tags {
    margin-top: 12px;
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
    margin: 0 0 12px;
    font-size: 16px;
    padding-bottom: 10px;
    border-bottom: 1px solid var(--border);
  }

  .category-list {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .category-item a {
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: var(--text-secondary);
    padding: 7px 0;
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
  .list-main .list-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .list-main .article-card {
    padding: 18px;
  }

  .list-main .page-title {
    font-size: 22px;
  }
}
</style>
