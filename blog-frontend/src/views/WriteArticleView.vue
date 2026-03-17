<template>
  <div class="site-shell write-article-page">
    <SiteHeader />
    
    <main class="page-main">
      <div class="container">
        <div class="write-container">
          <section class="write-card surface-card">
            <h2 class="write-title">{{ isEdit ? '编辑文章' : '写文章' }}</h2>
            
            <el-form
              ref="formRef"
              :model="articleForm"
              :rules="rules"
              label-width="80px"
              class="article-form"
            >
              <el-form-item label="标题" prop="title">
                <el-input v-model="articleForm.title" placeholder="请输入文章标题" maxlength="200" show-word-limit />
              </el-form-item>
              
              <el-form-item label="分类" prop="categoryId">
                <el-select v-model="articleForm.categoryId" placeholder="请选择分类" clearable>
                  <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  />
                </el-select>
              </el-form-item>
              
              <el-form-item label="标签" prop="tagIds">
                <el-select v-model="articleForm.tagIds" multiple placeholder="请选择标签">
                  <el-option
                    v-for="tag in tags"
                    :key="tag.id"
                    :label="tag.name"
                    :value="tag.id"
                  />
                </el-select>
              </el-form-item>
              
              <el-form-item label="摘要" prop="excerpt">
                <el-input
                  v-model="articleForm.excerpt"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入文章摘要（可选，不填则自动生成）"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
              
              <el-form-item label="内容" prop="content">
                <div class="editor-container">
                  <div class="editor-toolbar">
                    <el-button-group>
                      <el-button size="small" @click="insertMarkdown('**', '**')">B</el-button>
                      <el-button size="small" @click="insertMarkdown('*', '*')">I</el-button>
                      <el-button size="small" @click="insertMarkdown('~~', '~~')">S</el-button>
                    </el-button-group>
                    <el-button-group style="margin-left: 10px;">
                      <el-button size="small" @click="insertMarkdown('# ', '')">H1</el-button>
                      <el-button size="small" @click="insertMarkdown('## ', '')">H2</el-button>
                      <el-button size="small" @click="insertMarkdown('### ', '')">H3</el-button>
                    </el-button-group>
                    <el-button-group style="margin-left: 10px;">
                      <el-button size="small" @click="insertMarkdown('- ', '')">列表</el-button>
                      <el-button size="small" @click="insertMarkdown('1. ', '')">有序</el-button>
                      <el-button size="small" @click="insertMarkdown('> ', '')">引用</el-button>
                      <el-button size="small" @click="insertMarkdown('`', '`')">代码</el-button>
                      <el-button size="small" @click="insertCodeBlock">代码块</el-button>
                      <el-button size="small" @click="insertMarkdown('[', '](url)')">链接</el-button>
                      <el-button size="small" @click="insertMarkdown('![alt](', ')')">图片</el-button>
                    </el-button-group>
                  </div>
                  <el-input
                    ref="editorRef"
                    v-model="articleForm.content"
                    type="textarea"
                    :rows="20"
                    placeholder="请输入文章内容（支持 Markdown 格式）"
                    class="editor-textarea"
                  />
                </div>
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleSaveDraft" :loading="loading">
                  保存草稿
                </el-button>
                <el-button type="success" @click="handlePublish" :loading="loading">
                  发布文章
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store'
import { articleApi, categoryApi, tagApi } from '../api'
import { ElMessage } from 'element-plus'
import SiteHeader from '../components/SiteHeader.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const editorRef = ref(null)
const loading = ref(false)
const categories = ref([])
const tags = ref([])

const isEdit = ref(false)
const articleId = ref(null)

const articleForm = reactive({
  title: '',
  content: '',
  excerpt: '',
  categoryId: null,
  tagIds: [],
  status: 'draft'
})

const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { max: 200, message: '标题不能超过200个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ]
}

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const [categoriesRes, tagsRes] = await Promise.all([
      categoryApi.getCategories(),
      tagApi.getTags()
    ])
    if (categoriesRes.code === 200) categories.value = categoriesRes.data
    if (tagsRes.code === 200) tags.value = tagsRes.data
  } catch (error) {
    console.error('Failed to load data:', error)
  }

  if (route.params.id) {
    isEdit.value = true
    articleId.value = route.params.id
    loadArticle()
  }
})

const loadArticle = async () => {
  try {
    const res = await articleApi.getArticleById(articleId.value)
    if (res.code === 200) {
      const article = res.data.article || res.data
      if (!article) {
        ElMessage.error('文章不存在')
        router.push('/my-articles')
        return
      }
      articleForm.title = article.title || ''
      articleForm.content = article.content || ''
      articleForm.excerpt = article.excerpt || ''
      articleForm.categoryId = article.categoryId
      articleForm.tagIds = article.tags?.map(t => t.id) || []
      articleForm.status = article.status || 'draft'
    } else {
      ElMessage.error(res.message || '加载文章失败')
      router.push('/my-articles')
    }
  } catch (error) {
    console.error('Failed to load article:', error)
    ElMessage.error('加载文章失败')
    router.push('/my-articles')
  }
}

const insertMarkdown = (prefix, suffix) => {
  const textarea = editorRef.value?.$el?.querySelector('textarea')
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = articleForm.content.substring(start, end)
  const replacement = prefix + selectedText + suffix
  
  articleForm.content = 
    articleForm.content.substring(0, start) + 
    replacement + 
    articleForm.content.substring(end)
  
  textarea.focus()
  textarea.setSelectionRange(start + prefix.length, start + prefix.length + selectedText.length)
}

const insertCodeBlock = () => {
  const codeBlock = '\n```\n代码内容\n```\n'
  articleForm.content += codeBlock
}

const handleSaveDraft = async () => {
  articleForm.status = 'draft'
  await handleSubmit()
}

const handlePublish = async () => {
  articleForm.status = 'published'
  await handleSubmit()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        let res
        if (isEdit.value) {
          res = await articleApi.updateArticle(articleId.value, articleForm)
        } else {
          res = await articleApi.createArticle(articleForm)
        }
        
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '文章更新成功' : '文章创建成功')
          router.push('/my-articles')
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error('操作失败，请稍后重试')
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
.write-container {
  max-width: 900px;
  margin: 0 auto;
}

.write-card {
  padding: 32px;
}

.write-title {
  margin: 0 0 24px;
  font-size: 24px;
  color: var(--text);
}

.article-form {
  :deep(.el-select) {
    width: 100%;
  }
}

.editor-container {
  width: 100%;
}

.editor-toolbar {
  margin-bottom: 12px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.editor-textarea {
  :deep(.el-textarea__inner) {
    font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
    font-size: 14px;
    line-height: 1.6;
    border-radius: 8px;
  }
}

@media (max-width: 768px) {
  .write-card {
    padding: 20px 16px;
  }
  
  .write-title {
    font-size: 20px;
  }
  
  .editor-toolbar {
    padding: 8px;
  }
}
</style>
