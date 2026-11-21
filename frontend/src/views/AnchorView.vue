<template>
  <div class="anchor">
    <el-row :gutter="16">
      <el-col :xs="24" :md="14">
        <el-card>
          <template #header>创建直播间</template>
          <el-form :model="form" label-position="top">
            <el-form-item label="直播间标题" required>
              <el-input v-model="form.title" placeholder="例如：今晚冲王者段位" />
            </el-form-item>
            <el-form-item label="游戏分类">
              <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="封面图地址">
              <el-input v-model="form.cover" placeholder="封面图片 URL" />
            </el-form-item>
            <el-form-item label="直播间公告">
              <el-input v-model="form.notice" type="textarea" />
            </el-form-item>
            <el-button type="primary" :loading="creating" @click="create">创建直播间</el-button>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="10">
        <el-card v-if="room" class="push-info">
          <template #header>推流信息</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="直播间标题">{{ room.title }}</el-descriptions-item>
            <el-descriptions-item label="推流密钥">{{ room.streamKey }}</el-descriptions-item>
            <el-descriptions-item label="RTMP 推流地址">
              <code>{{ rtmpUrl }}</code>
            </el-descriptions-item>
          </el-descriptions>
          <el-alert
            class="tip"
            type="info"
            :closable="false"
            title="在 OBS「设置 → 推流」中填写上述地址，推流密钥单独填写，即可开始直播。"
          />
          <div class="room-actions">
            <el-button type="success" @click="start">开播</el-button>
            <el-button @click="stop">下播</el-button>
          </div>
        </el-card>
        <el-empty v-else description="创建直播间后在此查看推流地址" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { createRoom, startLive, stopLive, listCategories } from '@/api/live'

const categories = ref([])
const room = ref(null)
const creating = ref(false)
const form = reactive({ title: '', categoryId: null, cover: '', notice: '' })

// 与后端 gamelive.rtmp.push-prefix 保持一致
const rtmpPrefix = 'rtmp://localhost:1935/live/'
const rtmpUrl = computed(() => (room.value ? `${rtmpPrefix}${room.value.streamKey}` : ''))

const create = async () => {
  if (!form.title) {
    ElMessage.warning('请填写直播间标题')
    return
  }
  creating.value = true
  try {
    room.value = await createRoom(form)
    ElMessage.success('直播间创建成功')
  } finally {
    creating.value = false
  }
}

const start = async () => {
  await startLive(room.value.id)
  ElMessage.success('已开播')
}

const stop = async () => {
  await stopLive(room.value.id)
  ElMessage.success('已下播')
}

onMounted(async () => {
  categories.value = await listCategories()
})
</script>

<style scoped>
.push-info .tip {
  margin: 12px 0;
}
.room-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}
code {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
}
</style>
