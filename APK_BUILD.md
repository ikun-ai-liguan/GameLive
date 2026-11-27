# GameLive 安卓 APK 打包说明

前端已经通过 Capacitor 封装为原生安卓项目，源码位于 `frontend/android/`。本文件说明如何把 App 装到手机上，以及手机如何连接你电脑上的 GameLive 后端。

## 一、云端打包（推荐，无需安装 Android Studio）

1. 把代码推送到 GitHub 仓库 `main` 分支。
2. 打开仓库页面 → **Actions** → 左侧选择 **Build Android APK** → 点击 **Run workflow** → 确认运行。
   - 也可以推送一个 `v` 开头的标签（例如 `git tag v1.1 && git push origin v1.1`）自动触发。
3. 等待约 3–8 分钟，构建完成后进入该次运行页面，在 **Artifacts** 区域下载 `GameLive-debug-apk`（zip 包）。
4. 解压得到 `app-debug.apk`，传到手机安装即可（安卓需允许「安装未知来源应用」）。

> 产物是 debug 签名 APK，适合课程演示和自测；正式上架应用商店需要 release 签名，可另行配置。

## 二、本地打包（需要 Android Studio）

1. 安装 Android Studio（含 Android SDK，建议 API 34+）。
2. 用 Android Studio 打开 `frontend/android` 目录。
3. 首次打开会提示 Gradle 同步，等待依赖下载完成。
4. 菜单 **Build → Build Bundle(s) / APK(s) → Build APK(s)**。
5. 生成的 APK 在 `frontend/android/app/build/outputs/apk/debug/app-debug.apk`。

命令行方式（需配置好 `ANDROID_HOME`）：

```bash
cd frontend/android
./gradlew assembleDebug      # Windows 用 gradlew.bat assembleDebug
```

## 三、让手机 App 连接你的后端（关键）

**`localhost` 在手机里指的是手机自己，不是你的电脑。** 所以必须填写电脑的局域网 IP。

1. 确认手机和电脑连的是**同一个 Wi-Fi**。
2. 查电脑局域网 IP：
   - Windows：`ipconfig`，找「IPv4 地址」，通常是 `192.168.x.x`。
   - macOS / Linux：`ifconfig` 或 `ip addr`。
3. 启动后端（或 `docker compose up -d`），确保电脑防火墙放行 8080、8081、1935 端口。
4. 打开手机 App → 底部「我的」→「服务器设置」，填写：

| 项目 | 示例值 | 说明 |
| --- | --- | --- |
| API 地址 | `http://192.168.1.100:8080/api` | SpringBoot 后端 |
| WebSocket 地址 | `ws://192.168.1.100:8080` | 弹幕实时通信 |
| FLV 拉流前缀 | `http://192.168.1.100:8081/live/` | SRS HTTP-FLV |
| RTMP 推流前缀 | `rtmp://192.168.1.100:1935/live/` | OBS 主播推流 |

5. 点「测试连接」，显示「连接成功」后点「保存设置」。设置保存在手机本地，立即生效，无需重新打包。

## 四、端口说明

| 端口 | 用途 | 服务 |
| --- | --- | --- |
| 8080 | REST API + WebSocket | SpringBoot（context-path `/api`） |
| 8081 | HTTP-FLV 拉流 | SRS 流媒体 |
| 1935 | RTMP 推流 | SRS 流媒体 |
| 3306 | 数据库 | MySQL（仅电脑本地需要，手机不直连） |

## 五、常见问题

- **测试连接失败**：检查手机与电脑是否同一网络、电脑防火墙是否放行端口、后端是否启动、IP 是否填错。
- **App 打开是白屏**：确认执行过 `npm run build && npx cap sync android`，`dist` 目录有产物。
- **HTTPS 混合内容被拦截**：`capacitor.config.json` 已开启 `allowMixedContent`，AndroidManifest 已开启 `usesCleartextTraffic`，允许 HTTP 局域网访问。
- **弹幕连不上**：确认 WebSocket 地址填写的是 `ws://IP:8080`（不含 `/api`），App 会自行拼接 `/ws/danmu/{房间号}`。
