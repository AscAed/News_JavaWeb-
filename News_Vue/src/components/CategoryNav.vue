<template>
  <div class="category-nav">
    <div class="category-list">
      <router-link
        v-for="category in categories"
        :key="category.id"
        :class="{ active: selectedCategory === category.id }"
        :to="`/news?category=${category.id}`"
        class="category-item"
        @click="selectCategory(category.id)"
      >
        {{ category.name }}
      </router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import {useRoute} from 'vue-router'

const route = useRoute()
const selectedCategory = ref<number | null>(null)

const categories = ref([
  {id: 1, name: '时政要闻'},
  {id: 2, name: '财经资讯'},
  {id: 3, name: '科技前沿'},
  {id: 4, name: '体育竞技'},
  {id: 5, name: '娱乐八卦'},
  {id: 6, name: '教育文化'},
  {id: 7, name: '健康生活'},
  {id: 8, name: '国际新闻'}
])

const selectCategory = (categoryId: number) => {
  selectedCategory.value = categoryId
}

onMounted(() => {
  if (route.query.category) {
    selectedCategory.value = Number(route.query.category)
  }
})
</script>

<style scoped>
.category-nav {
  background: var(--bg-primary, #ffffff);
  padding: 12px 24px;
  border-bottom: 1px solid var(--border-primary, #ebeef5);
  margin-bottom: 20px;
}

.category-list {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  scrollbar-width: none; /* Firefox */
}

.category-list::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
}

.category-item {
  color: var(--text-primary, #303133);
  text-decoration: none;
  font-size: 16px;
  padding: 8px 16px;
  border-radius: 20px;
  white-space: nowrap;
  transition: all 0.3s;
}

.category-item:hover, .category-item.active {
  color: var(--primary-color, #409eff);
  background: rgba(64, 158, 255, 0.1);
  font-weight: 500;
}
</style>
