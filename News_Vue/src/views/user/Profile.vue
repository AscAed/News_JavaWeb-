<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="avatar-section">
        <el-avatar :size="80" :src="userStore.userInfo?.avatarUrl">
          {{ userStore.userInfo?.username?.charAt(0) }}
        </el-avatar>
        <h2>{{ userStore.userInfo?.username }}</h2>
        <p>{{ userStore.userInfo?.email }}</p>
      </div>
    </div>

    <div class="profile-content">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人信息" name="info">
          <div class="info-section">
            <el-form :model="profileForm" label-width="80px">
              <el-form-item label="用户名">
                <el-input v-model="profileForm.username" disabled />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="profileForm.phone" disabled />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="updateProfile">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <div class="password-section">
            <el-form :model="passwordForm" label-width="100px">
              <el-form-item label="当前密码">
                <el-input v-model="passwordForm.currentPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="passwordForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="changePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('info')

const profileForm = ref({
  username: '',
  email: '',
  phone: ''
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const updateProfile = async () => {
  try {
    // 调用更新个人信息API
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  try {
    // 调用修改密码API
    ElMessage.success('密码修改成功')
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    ElMessage.error('密码修改失败')
  }
}

onMounted(() => {
  if (userStore.userInfo) {
    profileForm.value = {
      username: userStore.userInfo.username || '',
      email: userStore.userInfo.email || '',
      phone: userStore.userInfo.phone || ''
    }
  }
})
</script>

<style scoped>
.profile-page {
  padding: var(--spacing-lg);
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.avatar-section h2 {
  margin: var(--spacing-md) 0 var(--spacing-sm);
  color: var(--text-primary);
}

.avatar-section p {
  color: var(--text-secondary);
  margin: 0;
}

.profile-content {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.info-section,
.password-section {
  padding: var(--spacing-lg);
}
</style>
