import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'home', component: () => import('@/views/HomeView.vue'), meta: { title: '首页' } },
  { path: '/product', name: 'product', component: () => import('@/views/ProductListView.vue'), meta: { title: '产品中心' } },
  { path: '/product/d/:id', name: 'product-detail', component: () => import('@/views/ProductDetailView.vue'), meta: { title: '产品详情' } },
  { path: '/booking', name: 'booking', component: () => import('@/views/BookingView.vue'), meta: { title: '预约体验' } },
  { path: '/store', name: 'store', component: () => import('@/views/StoreView.vue'), meta: { title: '门店查询' } },
  { path: '/news', name: 'news', component: () => import('@/views/NewsListView.vue'), meta: { title: '新闻资讯' } },
  { path: '/news/:id', name: 'news-detail', component: () => import('@/views/NewsDetailView.vue'), meta: { title: '资讯详情' } },
  { path: '/p/:slug', name: 'site-page', component: () => import('@/views/PageView.vue'), meta: { title: '页面' } },
  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFoundView.vue'), meta: { title: '页面不存在' } }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.afterEach(to => {
  const siteName = import.meta.env.VITE_SITE_NAME || 'site'
  document.title = to.meta && to.meta.title ? to.meta.title + ' - ' + siteName : siteName
})

export default router
