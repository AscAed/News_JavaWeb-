<template>
  <el-dialog
    v-model="visible"
    title="请完成安全验证"
    width="380px"
    :close-on-click-modal="false"
    :before-close="handleClose"
    center
    append-to-body
  >
    <div class="captcha-container" v-loading="loading">
      <div class="canvas-area" :style="{ width: bgWidth + 'px', height: bgHeight + 'px' }">
        <!-- 背景图 -->
        <img :src="bgBase64" class="bg-img" v-if="bgBase64" />
        
        <!-- 滑块图 -->
        <div 
          class="slider-piece"
          :style="{ 
            left: sliderX + 'px', 
            top: sliderY + 'px',
            backgroundImage: `url(${sliderBase64})`
          }"
          v-if="sliderBase64"
        ></div>
      </div>

      <div class="slider-track-container">
        <div class="slider-track">
          <div class="slider-text" v-show="!isDragging && !verifySuccess">{{ "向右滑动完成验证" }}</div>
          <div 
            class="slider-button" 
            :style="{ left: sliderX + 'px' }"
            @mousedown="onDragStart"
            @touchstart="onDragStart"
          >
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
      
      <div class="status-tip" :class="{ error: verifyError, success: verifySuccess }">
        {{ statusMsg }}
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import { getCaptcha, verifyCaptcha } from '@/api/modules/auth'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits(['update:modelValue', 'success', 'close'])

const visible = ref(false)
const loading = ref(false)
const bgWidth = 320
const bgHeight = 160
const bgBase64 = ref('')
const sliderBase64 = ref('')
const sliderY = ref(0)
const captchaKey = ref('')

const sliderX = ref(0)
const isDragging = ref(false)
const startX = ref(0)

const statusMsg = ref('')
const verifyError = ref(false)
const verifySuccess = ref(false)

watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal) {
    reset()
    loadCaptcha()
  }
})

const loadCaptcha = async () => {
  loading.value = true
  try {
    const res = await getCaptcha() as any
    if (res.code === 200) {
      bgBase64.value = res.data.backgroundBase64
      sliderBase64.value = res.data.sliderBase64
      sliderY.value = res.data.sliderY
      captchaKey.value = res.data.captchaKey
    }
  } catch (error) {
    ElMessage.error('加载验证码失败')
  } finally {
    loading.value = false
  }
}

const reset = () => {
  sliderX.value = 0
  verifyError.value = false
  verifySuccess.value = false
  statusMsg.value = ''
}

const handleClose = () => {
  emit('update:modelValue', false)
  emit('close')
}

const onDragStart = (e: MouseEvent | TouchEvent) => {
  if (verifySuccess.value) return
  isDragging.value = true
  if (e instanceof MouseEvent) {
    startX.value = e.clientX
  } else if (e.touches && e.touches[0]) {
    startX.value = e.touches[0].clientX
  }
  
  window.addEventListener('mousemove', onDragging)
  window.addEventListener('touchmove', onDragging)
  window.addEventListener('mouseup', onDragEnd)
  window.addEventListener('touchend', onDragEnd)
}

const onDragging = (e: MouseEvent | TouchEvent) => {
  if (!isDragging.value) return
  let currentX = 0
  if (e instanceof MouseEvent) {
    currentX = e.clientX
  } else if (e.touches && e.touches[0]) {
    currentX = e.touches[0].clientX
  } else {
    return
  }
  
  let moveX = currentX - startX.value
  
  // 限制范围
  if (moveX < 0) moveX = 0
  if (moveX > bgWidth - 50) moveX = bgWidth - 50 // 50是滑块宽度
  
  sliderX.value = moveX
}

const onDragEnd = async () => {
  if (!isDragging.value) return
  isDragging.value = false
  
  window.removeEventListener('mousemove', onDragging)
  window.removeEventListener('touchmove', onDragging)
  window.removeEventListener('mouseup', onDragEnd)
  window.removeEventListener('touchend', onDragEnd)
  
  // 发送验证
  try {
    const res = await verifyCaptcha({
      captchaKey: captchaKey.value,
      sliderX: Math.round(sliderX.value)
    }) as any
    
    if (res.code === 200) {
      verifySuccess.value = true
      statusMsg.value = '验证通过'
      setTimeout(() => {
        emit('success', res.data) // res.data is the captchaToken
        handleClose()
      }, 500)
    } else {
      verifyError.value = true
      statusMsg.value = '验证未通过，请重试'
      setTimeout(() => {
        reset()
        loadCaptcha()
      }, 1000)
    }
  } catch (error) {
    ElMessage.error('验证异常')
    reset()
    loadCaptcha()
  }
}

onUnmounted(() => {
  window.removeEventListener('mousemove', onDragging)
  window.removeEventListener('touchmove', onDragging)
  window.removeEventListener('mouseup', onDragEnd)
  window.removeEventListener('touchend', onDragEnd)
})
</script>

<style scoped>
.captcha-container {
  padding: 10px;
  user-select: none;
}
.canvas-area {
  position: relative;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.bg-img {
  width: 100%;
  height: 100%;
  display: block;
}
.slider-piece {
  position: absolute;
  width: 50px;
  height: 50px;
  background-size: 50px 50px;
  z-index: 2;
  box-shadow: 0 0 10px rgba(0,0,0,0.3);
}
.slider-track-container {
  position: relative;
  height: 40px;
  background: #f7f9fa;
  border: 1px solid #e4e7eb;
  border-radius: 20px;
}
.slider-track {
  position: relative;
  width: 100%;
  height: 100%;
}
.slider-text {
  position: absolute;
  width: 100%;
  text-align: center;
  line-height: 40px;
  font-size: 14px;
  color: #666;
}
.slider-button {
  position: absolute;
  top: -1px;
  left: 0;
  width: 40px;
  height: 40px;
  background: #fff;
  border: 1px solid #e4e7eb;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 0 5px rgba(0,0,0,0.1);
  z-index: 3;
  transition: box-shadow 0.2s;
}
.slider-button:hover {
  background: #f4f4f4;
  box-shadow: 0 0 8px rgba(0,0,0,0.2);
}
.status-tip {
  margin-top: 10px;
  text-align: center;
  height: 20px;
  font-size: 13px;
  transition: color 0.3s;
}
.status-tip.error {
  color: #f56c6c;
}
.status-tip.success {
  color: #67c23a;
}
</style>
