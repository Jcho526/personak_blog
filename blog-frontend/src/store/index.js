import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    currentUser: (state) => state.user
  },
  actions: {
    setUser(user) {
      this.user = user
    },
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    logout() {
      this.user = null
      this.token = ''
      localStorage.removeItem('token')
    }
  }
})

export const useArticleStore = defineStore('article', {
  state: () => ({
    articles: [],
    currentArticle: null,
    loading: false
  }),
  getters: {
    articleList: (state) => state.articles
  },
  actions: {
    setArticles(articles) {
      this.articles = articles
    },
    setCurrentArticle(article) {
      this.currentArticle = article
    },
    setLoading(loading) {
      this.loading = loading
    }
  }
})