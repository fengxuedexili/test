<template>
  <div>
    <BannerCarousel v-if="banners.length" :items="banners" />
    <section v-else class="hero">
      <div class="container">
        <h1>{{ siteName }}　让厨房更懂生活</h1>
        <p>厨房电器 · 品质生活 · 一站式体验</p>
        <router-link class="btn btn-primary" to="/product">进入产品中心</router-link>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <div class="section-head">
          <h2>产品分类</h2>
          <span class="en">CATEGORIES</span>
        </div>
        <div class="chips" v-if="flatCategories.length">
          <router-link v-for="child in flatCategories" :key="child.id" class="chip"
                       :to="{ path: '/product', query: { categoryId: child.id } }">{{ child.name }}</router-link>
        </div>
        <div v-else class="empty">暂无栏目</div>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <div class="section-head">
          <h2>明星产品</h2>
          <router-link to="/product">查看全部 →</router-link>
        </div>
        <div class="product-grid" v-if="products.length">
          <ProductCard v-for="item in products" :key="item.id" :product="item" />
        </div>
        <div v-else class="empty">{{ loading ? '加载中…' : '暂无推荐产品' }}</div>
      </div>
    </section>

    <section class="section" v-if="articles.length">
      <div class="container">
        <div class="section-head">
          <h2>最新资讯</h2>
          <router-link to="/news">查看全部 →</router-link>
        </div>
        <div class="news-strip">
          <router-link v-for="item in articles" :key="item.id" class="news-strip-item" :to="'/news/' + item.id">
            <div class="date">{{ item.publishTime }}</div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.summary }}</p>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useStore } from 'vuex'
import { getArticlePage, getBanners, getCategoryTree, getRecommendProducts } from '@/api/site'
import BannerCarousel from '@/components/BannerCarousel.vue'
import ProductCard from '@/components/ProductCard.vue'
import { setJsonLd, setSeo } from '@/utils/seo'

const store = useStore()
const banners = ref([])
const categories = ref([])
const products = ref([])
const articles = ref([])
const loading = ref(true)

const siteName = computed(() => store.state.config.info.name || import.meta.env.VITE_SITE_NAME || 'site')
const flatCategories = computed(() => {
  const list = []
  categories.value.forEach(root => {
    const children = root.children && root.children.length ? root.children : [root]
    children.forEach(child => list.push(child))
  })
  return list
})

async function loadAll() {
  try {
    const [bannerList, tree, recommends, articleData] = await Promise.all([
      getBanners('home'),
      getCategoryTree(1),
      getRecommendProducts(6),
      getArticlePage({ pageNum: 1, pageSize: 3 })
    ])
    banners.value = bannerList || []
    categories.value = tree || []
    products.value = recommends || []
    articles.value = (articleData && articleData.rows) || []
  } catch (e) {
    banners.value = []
    categories.value = []
    products.value = []
    articles.value = []
  } finally {
    loading.value = false
  }
}

// 后台改完内容后切回前台标签页自动刷新（无需手动 F5）
function onVisible() {
  if (document.visibilityState === 'visible') {
    loadAll()
  }
}

onMounted(() => {
  setSeo({ title: siteName.value, description: 'site 官网，厨房电器产品与体验' })
  setJsonLd('organization', {
    '@context': 'https://schema.org',
    '@type': 'Organization',
    name: siteName.value,
    url: window.location.origin
  })
  loadAll()
  document.addEventListener('visibilitychange', onVisible)
})

onUnmounted(() => {
  document.removeEventListener('visibilitychange', onVisible)
})
</script>
