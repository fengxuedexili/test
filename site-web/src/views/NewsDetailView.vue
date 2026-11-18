<template>
  <div class="container article-page">
    <div v-if="error" class="empty">
      <p class="error-text">{{ error }}</p>
      <router-link class="btn btn-primary" to="/news">返回资讯列表</router-link>
    </div>

    <article v-else-if="article">
      <div class="article-head">
        <div class="news-meta">
          <span>{{ article.publishTime }}</span>
          <span v-if="article.author">· {{ article.author }}</span>
          <span>· 浏览 {{ article.viewCount }}</span>
        </div>
        <h1>{{ article.title }}</h1>
        <p class="article-summary">{{ article.summary }}</p>
      </div>

      <img v-if="article.cover" class="article-cover" :src="resolveImageUrl(article.cover)"
           :alt="article.title" @error="onImgError" />

      <div class="rich-content" v-html="safeContent"></div>

      <div class="panel" v-if="article.related && article.related.length">
        <h2>相关阅读</h2>
        <div class="news-list compact">
          <router-link v-for="item in article.related" :key="item.id" class="news-item" :to="'/news/' + item.id">
            <div class="news-body">
              <div class="news-meta">{{ item.publishTime }}</div>
              <h3>{{ item.title }}</h3>
            </div>
          </router-link>
        </div>
      </div>

      <div class="article-back">
        <router-link class="btn" to="/news">← 返回资讯列表</router-link>
      </div>
    </article>

    <div v-else class="empty">加载中…</div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '@/api/site'
import { onImgError, resolveImageUrl } from '@/utils/image'
import { sanitizeHtml } from '@/utils/sanitize'
import { absoluteUrl, setJsonLd, setSeo } from '@/utils/seo'

const route = useRoute()
const article = ref(null)
const error = ref('')

const safeContent = computed(() => sanitizeHtml(article.value && article.value.content))

function load() {
  error.value = ''
  article.value = null
  getArticleDetail(route.params.id).then(data => {
    article.value = data
    const seo = (data && data.seo) || {}
    const cover = data.cover ? absoluteUrl(resolveImageUrl(data.cover)) : undefined
    setSeo({
      title: seo.title || (data.title + ' - ' + import.meta.env.VITE_SITE_NAME),
      description: seo.description || data.summary,
      keywords: seo.keywords,
      ogImage: cover
    })
    setJsonLd('article', {
      '@context': 'https://schema.org',
      '@type': 'NewsArticle',
      headline: data.title,
      description: seo.description || data.summary || data.title,
      datePublished: data.publishTime,
      author: {
        '@type': 'Person',
        name: data.author || import.meta.env.VITE_SITE_NAME || 'site'
      },
      image: cover,
      mainEntityOfPage: window.location.href
    })
  }).catch(err => {
    error.value = err.message || '文章不存在或已下线'
  })
}

watch(() => route.params.id, () => {
  if (route.name === 'news-detail') {
    load()
  }
}, { immediate: true })
</script>
