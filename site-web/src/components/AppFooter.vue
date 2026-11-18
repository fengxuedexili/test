<template>
  <footer class="site-footer">
    <div class="container inner">
      <span>{{ config.copyright || '© 2026 site' }}</span>
      <span>
        <template v-if="config.phone">客服电话：{{ config.phone }}　</template>
        <template v-if="config.email">邮箱：{{ config.email }}</template>
      </span>
      <span v-if="config.icp">{{ config.icp }}</span>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const config = computed(() => store.state.config.info || {})
</script>
