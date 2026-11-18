<template>
  <div class="container page-view">
    <div v-if="error" class="empty">
      <p class="error-text">{{ error }}</p>
      <router-link class="btn btn-primary" to="/">返回首页</router-link>
    </div>

    <template v-else-if="page">
      <div class="section-head" style="padding-top: 32px">
        <h2>{{ page.title }}</h2>
        <span class="en">PAGE</span>
      </div>

      <div class="blocks">
        <template v-for="(block, index) in page.blocks" :key="index">
          <div v-if="block.type === 'richText'" class="rich-content"
               v-html="sanitizeHtml(block.props && block.props.html)"></div>

          <div v-else-if="block.type === 'imageText'" class="image-text"
               :class="{ reverse: block.props && block.props.reverse }">
            <img v-if="block.props && block.props.image"
                 :src="resolveImageUrl(block.props.image)" :alt="block.props.title || ''" @error="onImgError" />
            <div class="image-text-body">
              <h3 v-if="block.props && block.props.title">{{ block.props.title }}</h3>
              <p v-if="block.props && block.props.text">{{ block.props.text }}</p>
            </div>
          </div>
        </template>
      </div>
    </template>

    <div v-else class="empty">加载中…</div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getPage } from '@/api/site'
import { onImgError, resolveImageUrl } from '@/utils/image'
import { sanitizeHtml } from '@/utils/sanitize'
import { setSeo } from '@/utils/seo'

const route = useRoute()
const page = ref(null)
const error = ref('')

function load() {
  error.value = ''
  page.value = null
  getPage(route.params.slug).then(data => {
    page.value = data
    const seo = (data && data.seo) || {}
    setSeo({
      title: seo.title || (data.title + ' - ' + import.meta.env.VITE_SITE_NAME),
      description: seo.description,
      keywords: seo.keywords
    })
  }).catch(err => {
    error.value = err.message || '页面不存在或已下线'
  })
}

watch(() => route.params.slug, () => {
  if (route.name === 'site-page') {
    load()
  }
}, { immediate: true })
</script>
