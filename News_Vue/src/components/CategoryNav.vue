<template>
  <div class="category-nav">
    <div class="category-list">
      <a
        v-for="category in newsStore.newsTypes"
        :key="category.tid"
        :class="{ active: selectedCategory === category.tid }"
        href="javascript:void(0)"
        class="category-item"
        @click="selectCategory(category.tid)"
      >
        {{ category.tname }}
      </a>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useNewsStore } from '@/stores/news'

const route = useRoute()
const newsStore = useNewsStore()
const selectedCategory = ref<number | string | null>(0)

const selectCategory = (categoryId: number | string) => {
  selectedCategory.value = categoryId
  // Dispatch typeChange event for HomeView to handle the filtering
  window.dispatchEvent(
    new CustomEvent('typeChange', {
      detail: { type: categoryId },
    })
  )
}

// Reset selection when source changes (via HomeView resetting selectedType)
// Watch the store changes and default selection to All (0)
watch(() => newsStore.newsTypes, () => {
  if (newsStore.newsTypes && newsStore.newsTypes.length > 0) {
    selectedCategory.value = newsStore.newsTypes[0]?.tid ?? 0;
  }
})

onMounted(() => {
  if (route.query.category) {
    selectedCategory.value = isNaN(Number(route.query.category))
      ? route.query.category as string
      : Number(route.query.category)
  }
})
</script>

<style scoped>
.category-nav {
  position: sticky;
  top: 72px; /* AppHeader height */
  z-index: 990;
  background: var(--bg-glass);
  backdrop-filter: var(--blur-md);
  -webkit-backdrop-filter: var(--blur-md);
  border-bottom: 1px solid var(--border-primary);
  padding: 0 var(--spacing-xl);
}

.category-list {
  display: flex;
  gap: var(--spacing-lg);
  overflow-x: auto;
  scrollbar-width: none; /* Firefox */
  max-width: var(--container-max);
  margin: 0 auto;
}

.category-list::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
}

.category-item {
  position: relative;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: var(--text-sm);
  font-weight: 500;
  padding: var(--spacing-md) 0;
  white-space: nowrap;
  transition: color var(--transition-fast);
}

.category-item:hover {
  color: var(--primary-color);
}

.category-item.active {
  color: var(--primary-color);
  font-weight: 600;
}

/* Animated Underline */
.category-item::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: var(--primary-color);
  border-radius: 3px 3px 0 0;
  transform: scaleX(0);
  transition: transform var(--transition-fast) ease-out;
}

.category-item.active::after {
  transform: scaleX(1);
}
</style>
