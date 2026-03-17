import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/article/:id',
      name: 'ArticleDetail',
      component: () => import('../views/ArticleDetailView.vue')
    },
    {
      path: '/category/:id',
      name: 'Category',
      component: () => import('../views/CategoryView.vue')
    },
    {
      path: '/tag/:id',
      name: 'Tag',
      component: () => import('../views/TagView.vue')
    },
    {
      path: '/about',
      name: 'About',
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/change-password',
      name: 'ChangePassword',
      component: () => import('../views/ChangePasswordView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/write',
      name: 'WriteArticle',
      component: () => import('../views/WriteArticleView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/edit/:id',
      name: 'EditArticle',
      component: () => import('../views/WriteArticleView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-articles',
      name: 'MyArticles',
      component: () => import('../views/MyArticlesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-favorites',
      name: 'MyFavorites',
      component: () => import('../views/MyFavoritesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-likes',
      name: 'MyLikes',
      component: () => import('../views/MyLikesView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

export default router
