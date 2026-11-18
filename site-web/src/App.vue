<template>
  <div class="site">
    <AppHeader />
    <main class="site-main">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useStore } from 'vuex'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const store = useStore()

onMounted(() => {
  store.dispatch('config/load')
})
</script>
