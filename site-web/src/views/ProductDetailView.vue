<template>
  <div class="container">
    <div v-if="error" class="empty">
      <p class="error-text">{{ error }}</p>
      <router-link class="btn btn-primary" to="/product">返回产品中心</router-link>
    </div>

    <template v-else-if="product">
      <div class="detail-top">
        <div class="gallery">
          <div class="main">
            <img :src="activeImage" :alt="product.name" @error="onImgError" />
          </div>
          <div class="thumbs" v-if="images.length > 1">
            <img v-for="(img, index) in images" :key="index" :src="img"
                 :class="{ active: index === activeIndex }" @click="activeIndex = index" @error="onImgError" />
          </div>
        </div>

        <div class="detail-info">
          <div class="model">{{ product.model }}</div>
          <h1>{{ product.name }}</h1>
          <p class="subtitle">{{ product.subtitle }}</p>
          <div class="tags" v-if="product.tags && product.tags.length">
            <span class="tag" v-for="tag in product.tags" :key="tag">{{ tag }}</span>
          </div>
          <div class="points" v-if="product.sellingPoints && product.sellingPoints.length">
            <div class="point" v-for="(point, index) in product.sellingPoints" :key="index">
              <strong>{{ point.title }}</strong>
              <span>{{ point.desc }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="panel" v-if="product.paramGroups && product.paramGroups.length">
        <h2>规格参数</h2>
        <div class="param-group" v-for="(group, gi) in product.paramGroups" :key="gi">
          <h4>{{ group.group }}</h4>
          <table class="param-table">
            <tbody>
              <tr v-for="(item, ii) in group.items" :key="ii">
                <th>{{ item.label }}</th>
                <td>{{ item.value }} {{ item.unit }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="panel" v-if="detailImages.length">
        <h2>产品看点</h2>
        <div class="detail-gallery">
          <figure v-for="(item, index) in detailImages" :key="index" class="detail-figure">
            <img :src="item.url" :alt="item.caption || product.name" loading="lazy" @error="onImgError" />
            <figcaption v-if="item.caption">{{ item.caption }}</figcaption>
          </figure>
        </div>
      </div>

      <div class="panel" v-if="product.downloads && product.downloads.length">
        <h2>资料下载</h2>
        <ul class="downloads">
          <li v-for="(file, index) in product.downloads" :key="index">
            <a :href="file.url" target="_blank" rel="noopener">{{ file.name }}</a>
            <span v-if="file.size">（{{ file.size }}）</span>
          </li>
        </ul>
      </div>

      <div class="panel" v-if="related.length">
        <h2>相关推荐</h2>
        <div class="product-grid">
          <ProductCard v-for="item in related" :key="item.id" :product="item" />
        </div>
      </div>
    </template>

    <div v-else class="empty">加载中…</div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getProductDetail } from '@/api/site'
import ProductCard from '@/components/ProductCard.vue'
import { PLACEHOLDER, onImgError, resolveImageUrl } from '@/utils/image'
import { absoluteUrl, setJsonLd, setSeo } from '@/utils/seo'

const route = useRoute()
const product = ref(null)
const related = ref([])
const error = ref('')
const activeIndex = ref(0)

const images = computed(() => {
  const p = product.value
  if (!p) {
    return []
  }
  const list = [p.cover].concat(p.images || []).filter(Boolean).map(resolveImageUrl)
  const unique = []
  list.forEach(url => {
    if (unique.indexOf(url) === -1) {
      unique.push(url)
    }
  })
  return unique.length ? unique : [PLACEHOLDER]
})

const activeImage = computed(() => images.value[activeIndex.value] || PLACEHOLDER)

const detailImages = computed(() => {
  const list = (product.value && product.value.detailImages) || []
  return list
    .filter(item => item && item.url)
    .map(item => ({ url: resolveImageUrl(item.url), caption: item.caption || '' }))
})

function load() {
  error.value = ''
  product.value = null
  related.value = []
  activeIndex.value = 0
  const id = route.params.id
  getProductDetail(id).then(data => {
    product.value = data
    related.value = (data && data.related) || []
    const seo = (data && data.seo) || {}
    const gallery = ((data.images && data.images.length ? data.images : [data.cover]) || [])
      .filter(Boolean).map(item => absoluteUrl(resolveImageUrl(item)))
    setSeo({
      title: seo.title || (data.name + ' - ' + import.meta.env.VITE_SITE_NAME),
      description: seo.description || data.subtitle,
      keywords: seo.keywords,
      ogImage: gallery[0]
    })
    setJsonLd('product', {
      '@context': 'https://schema.org',
      '@type': 'Product',
      name: data.name,
      sku: data.model,
      image: gallery,
      description: seo.description || data.subtitle || data.name,
      offers: {
        '@type': 'Offer',
        priceCurrency: 'CNY',
        availability: 'https://schema.org/InStock',
        url: window.location.href
      }
    })
    setJsonLd('breadcrumb', {
      '@context': 'https://schema.org',
      '@type': 'BreadcrumbList',
      itemListElement: [
        { '@type': 'ListItem', position: 1, name: '首页', item: window.location.origin + '/' },
        { '@type': 'ListItem', position: 2, name: '产品中心', item: window.location.origin + '/product' },
        { '@type': 'ListItem', position: 3, name: data.name, item: window.location.href }
      ]
    })
  }).catch(err => {
    error.value = err.message || '产品不存在或已下线'
  })
}

watch(() => route.params.id, () => {
  if (route.name === 'product-detail') {
    load()
  }
}, { immediate: true })
</script>
