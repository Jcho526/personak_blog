import axios from 'axios'
import { useUserStore } from '../store'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// API接口
export const articleApi = {
  getArticles: (params) => api.get('/articles', { params }),
  getArticleById: (id) => api.get(`/articles/${id}`),
  getArticlesByCategory: (categoryId, params) => api.get(`/categories/${categoryId}/articles`, { params }),
  getArticlesByTag: (tagId, params) => api.get(`/tags/${tagId}/articles`, { params }),
  createArticle: (data) => api.post('/articles', data),
  updateArticle: (id, data) => api.put(`/articles/${id}`, data),
  deleteArticle: (id) => api.delete(`/articles/${id}`),
  getMyArticles: () => api.get('/user/articles'),
  getMyLikes: () => api.get('/user/likes'),
  getMyFavorites: () => api.get('/user/favorites'),
  likeArticle: (id) => api.post(`/articles/${id}/like`),
  unlikeArticle: (id) => api.delete(`/articles/${id}/like`),
  favoriteArticle: (id) => api.post(`/articles/${id}/favorite`),
  unfavoriteArticle: (id) => api.delete(`/articles/${id}/favorite`)
}

export const userApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getUserInfo: () => api.get('/user/info'),
  updateProfile: (data) => api.put('/user/profile', data),
  changePassword: (data) => api.put('/user/password', data),
  uploadAvatar: (formData) => api.post('/user/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const categoryApi = {
  getCategories: () => api.get('/categories')
}

export const tagApi = {
  getTags: () => api.get('/tags')
}

export const commentApi = {
  getCommentsByArticleId: (articleId, params) => api.get(`/articles/${articleId}/comments`, { params }),
  addComment: (articleId, data) => api.post(`/articles/${articleId}/comments`, data)
}

export default api