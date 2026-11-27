<template>
  <div class="settings">
    <el-card>
      <template #header>
        <div class="card-head">
          <span>服务器与播放器设置</span>
          <el-tag type="info" size="small">无需重新打包</el-tag>
        </div>
      </template>

      <el-alert
        type="info"
        :closable="false"
        show-icon
        title="手机 App 里 localhost 指的是手机本身。请把下面的 localhost 改成电脑的局域网 IP（例如 192.168.1.100），手机与电脑需在同一 Wi-Fi。"
        class="tip"
      />

      <el-form :model="form" label-position="top" class="form">
        <el-form-item label="API 地址（后端 /api）">
          <el-input v-model="form.apiBaseUrl" placeholder="http://192.168.1.100:8080/api" />
        </el-form-item>
        <el-form-item label="WebSocket 地址（弹幕）">
          <el-input v-model="form.wsBaseUrl" placeholder="ws://192.168.1.100:8080" />
        </el-form-item>
        <el-form-item label="FLV 拉流前缀">
          <el-input v-model="form.flvPrefix" placeholder="http://192.168.1.100:8081/live/" />
        </el-form-item>
        <el-form-item label="RTMP 推流前缀（主播 OBS 用）">
          <el-input v-model="form.rtmpPrefix" placeholder="rtmp://192.168.1.100:1935/live/" />
        </el-form-item>
      </el-form>

      <div class="actions">
        <el-button type="primary" :loading="testing" @click="testConnection">测试连接</el-button>
        <el-button @click="save">保存设置</el-button>
        <el-button text @click="reset">恢复默认</el-button>
      </div>
      <div v-if="testResult" class="test-result" :class="{ ok: testOk }">{{ testResult }}</div>

      <el-divider />
      <div class="about">
        <h3>关于 GameLive</h3>
        <p>版本 1.0.0 · Vue 3 + SpringBoot + MySQL · 支持直播观看、弹幕、礼物、点赞、关注、历史、榜单与后台管理。</p>
        <p>端口说明：后端 8080、HTTP-FLV 8081、RTMP 1935。首次使用请先在后端初始化数据库。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import {
  serverConfig,
  getCurrentConfig,
  saveServerConfig,
  resetServerConfig
} from '@/config'

const form = reactive({
  apiBaseUrl: '',
  wsBaseUrl: '',
  flvPrefix: '',
  rtmpPrefix: ''
})

const testing = ref(false)
const testResult = ref('')
const testOk = ref(false)

const syncForm = () => {
  const current = getCurrentConfig()
  Object.keys(form).forEach((key) => {
    form[key] = current[key]
  })
}

const save = () => {
  const empty = Object.keys(form).find((key) => !form[key]?.trim())
  if (empty) {
    ElMessage.warning('请填写完整的服务器地址')
    return
  }
  Object.keys(form).forEach((key) => {
    form[key] = form[key].trim()
  })
  saveServerConfig({ ...form })
  ElMessage.success('设置已保存，立即生效')
}

const reset = () => {
  resetServerConfig()
  syncForm()
  testResult.value = ''
  ElMessage.success('已恢复默认设置')
}

const testConnection = async () => {
  const api = String(form.apiBaseUrl || '').trim().replace(/\/$/, '')
  if (!api) {
    ElMessage.warning('请先填写 API 地址')
    return
  }
  testing.value = true
  testResult.value = ''
  try {
    const response = await axios.get(`${api}/categories`, { timeout: 8000 })
    const ok = response.status >= 200 && response.status < 300
    testOk.value = ok
    testResult.value = ok ? '连接成功：后端可访问 ✓' : `连接异常：HTTP ${response.status}`
  } catch (error) {
    testOk.value = false
    testResult.value = `连接失败：${error.message || '无法访问后端'}（请检查 IP、端口与防火墙）`
  } finally {
    testing.value = false
  }
}

onMounted(syncForm)
</script>

<style scoped>
.settings { max-width: 760px; margin: 0 auto; }
.card-head { display: flex; align-items: center; gap: 8px; }
.tip { margin-bottom: 18px; }
.form { margin-top: 8px; }
.actions { display: flex; gap: 10px; flex-wrap: wrap; }
.test-result { margin-top: 12px; padding: 10px 12px; border-radius: 6px; background: #fef0f0; color: #c45656; font-size: 13px; }
.test-result.ok { background: #f0f9eb; color: #529b2e; }
.about h3 { margin: 0 0 8px; font-size: 15px; }
.about p { margin: 4px 0; color: #606266; font-size: 13px; line-height: 1.6; }
</style>
