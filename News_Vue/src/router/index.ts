import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from '@/stores/user'

// 路由守卫
const requireAuth = (to: any, from: any, next: any) => {
  const userStore = useUserStore()

  if (!userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 首页
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { title: '首页' },
    },

    // 新闻相关
    {
      path: '/news',
      name: 'newsList',
      component: () => import('@/views/news/NewsList.vue'),
      meta: { title: '新闻列表' },
    },
    {
      path: '/news/:hid',
      name: 'newsDetail',
      component: () => import('@/views/news/NewsDetail.vue'),
      meta: { title: '新闻详情' },
    },

    // 用户相关
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { title: '登录', guest: true, layout: 'empty' },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { title: '注册', guest: true, layout: 'empty' },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/user/Profile.vue'),
      beforeEnter: requireAuth,
      meta: { title: '个人中心' },
    },

    // 管理相关
    {
      path: '/admin',
      name: 'admin',
      redirect: '/admin/dashboard',
      beforeEnter: requireAuth,
      meta: { title: '管理后台', requiresAdmin: true },
    },
    {
      path: '/admin/dashboard',
      name: 'adminDashboard',
      component: () => import('@/views/admin/Dashboard.vue'),
      beforeEnter: requireAuth,
      meta: { title: '仪表盘', requiresAdmin: true },
    },
    {
      path: '/admin/news',
      name: 'adminNews',
      component: () => import('@/views/admin/NewsManage.vue'),
      beforeEnter: requireAuth,
      meta: { title: '新闻管理', requiresAdmin: true },
    },
    {
      path: '/admin/news/create',
      name: 'createNews',
      component: () => import('@/views/admin/CreateNews.vue'),
      beforeEnter: requireAuth,
      meta: { title: '创建新闻', requiresAdmin: true },
    },
    {
      path: '/admin/news/:hid/edit',
      name: 'editNews',
      component: () => import('@/views/admin/EditNews.vue'),
      beforeEnter: requireAuth,
      meta: { title: '编辑新闻', requiresAdmin: true },
    },

    // 收藏列表
    {
      path: '/favorites',
      name: 'favorites',
      component: () => import('@/views/user/Favorites.vue'),
      beforeEnter: requireAuth,
      meta: { title: '我的收藏' },
    },

    // 404页面
    {
      path: '/:pathMatch(.*)*',
      name: 'notFound',
      component: () => import('@/views/error/NotFound.vue'),
      meta: { title: '页面不存在' },
    },
  ],
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - 易闻趣事`
  }

  // 检查是否需要登录
  if (to.meta?.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
    return
  }

  // 检查是否需要管理员权限
  if (to.meta?.requiresAdmin && userStore.userInfo?.id !== 1) {
    next('/')
    return
  }

  // 已登录用户访问登录/注册页面时重定向到首页
  if (userStore.isLoggedIn && to.meta?.guest) {
    next('/')
    return
  }

  next()
})

export default router
