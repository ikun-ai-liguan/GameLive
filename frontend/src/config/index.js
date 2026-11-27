import { reactive } from 'vue'

/**
 * 运行时服务端配置。
 * 优先级：localStorage 覆盖 > public/app-config.json > 内置默认值。
 * 手机 App 无需重新打包，只要在「设置」页修改并保存即可切换服务器。
 */
export const STORAGE_KEY = 'gamelive_server_config'

export const DEFAULT_CONFIG = {
  apiBaseUrl: 'http://localhost:8080/api',
  wsBaseUrl: 'ws://localhost:8080',
  flvPrefix: 'http://localhost:8081/live/',
  rtmpPrefix: 'rtmp://localhost:1935/live/'
}

const KEYS = Object.keys(DEFAULT_CONFIG)

// 使用 reactive 对象，方便界面实时响应配置变化
export const serverConfig = reactive({ ...DEFAULT_CONFIG })

let fileConfig = {}
let loaded = false

// 只接受非空字符串，避免错误配置把地址清空
const normalize = (raw) => {
  const result = {}
  if (!raw || typeof raw !== 'object') return result
  KEYS.forEach((key) => {
    const value = raw[key]
    if (typeof value === 'string' && value.trim()) {
      result[key] = value.trim()
    }
  })
  return result
}

const readOverride = () => {
  try {
    return normalize(JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}'))
  } catch (error) {
    return {}
  }
}

const writeOverride = (config) => {
  const payload = {}
  KEYS.forEach((key) => {
    payload[key] = config[key]
  })
  localStorage.setItem(STORAGE_KEY, JSON.stringify(payload))
}

/**
 * 应用启动时调用：读取 app-config.json，再叠加本地覆盖。
 * 网络失败时静默降级到内置默认值，保证 App 仍能打开。
 */
export const loadConfig = async () => {
  try {
    const response = await fetch('/app-config.json', { cache: 'no-store' })
    if (response.ok) {
      fileConfig = normalize(await response.json())
    }
  } catch (error) {
    fileConfig = {}
  }
  Object.assign(serverConfig, DEFAULT_CONFIG, fileConfig, readOverride())
  loaded = true
  return serverConfig
}

/** 保存用户自定义地址，立即生效并持久化 */
export const saveServerConfig = (config = {}) => {
  const next = normalize(config)
  Object.assign(serverConfig, next)
  writeOverride(serverConfig)
  loaded = true
  return { ...serverConfig }
}

/** 恢复为 app-config.json（或内置默认）里的地址 */
export const resetServerConfig = () => {
  localStorage.removeItem(STORAGE_KEY)
  Object.assign(serverConfig, DEFAULT_CONFIG, fileConfig)
  loaded = true
  return { ...serverConfig }
}

export const isConfigLoaded = () => loaded

export const getCurrentConfig = () => ({ ...serverConfig })

export const getApiBaseUrl = () => serverConfig.apiBaseUrl || DEFAULT_CONFIG.apiBaseUrl
export const getWsBaseUrl = () => serverConfig.wsBaseUrl || DEFAULT_CONFIG.wsBaseUrl
export const getFlvPrefix = () => serverConfig.flvPrefix || DEFAULT_CONFIG.flvPrefix
export const getRtmpPrefix = () => serverConfig.rtmpPrefix || DEFAULT_CONFIG.rtmpPrefix
