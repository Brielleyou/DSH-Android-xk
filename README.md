# DSH 社区改版（非官方）

> 基于 [@jackwener](https://github.com/jackwener) 的 [DSH-Android](https://github.com/jackwener/DSH-Android)（Apache-2.0）修改而来。
> **非官方版本**：与原作者及 DeepSeek 官方无隶属关系，也未获其背书；请勿当作官方发行版安装或传播。

## 这是什么

在 DSH-Android v0.3.11 基础上的一批个人改动。改版使用独立包名、自签名证书，与官方发行版互不影响，**可与官方版并存安装，数据各自独立**。

| 项目 | 官方 0.3.11 | 本改版 |
| --- | --- | --- |
| 包名 | `io.github.jackwener.dsh` | `io.dshjsqb.xk` |
| 签名 | 官方证书 | 自签名（Debug 证书） |
| 数据目录 | 独立 | 独立，互不影响 |
| 并存安装 | — | 可以 |

## 改动清单（相对上游 0.3.11）

**聊天与界面**
- 代码块右上角「一键复制」
- 回复操作栏新增「查看原文」，完整查看/复制被挤压的表格与行内代码
- 修复：模型输出结束后聊天页跳回更早消息
- 修复：语言选择「跟随系统」被截断成「跟随系」
- 移除输入框快捷「权限 / 会话模式」按钮，收进「+」菜单

**功能**
- 扩展 → 技能：每个技能可「卸载」（含其他来源）
- 设置 → 手机权限：新增「root 授权」；Shizuku 与 root 拆成两个独立区块；显示已安装的 root 管理器（Magisk / KernelSU / APatch / SuperSU）及版本
- 设置 → 手机权限：新增「无障碍保活」（DSH 通知触发 + 每 30 分钟兜底，异常时自动重新绑定无障碍服务）
- 设置 → 新增「存储」：已占用明细 / 清理 / 备份 / 导入数据
- 输入法跨屏修复：两个输入法同时运行时，不再把文本写进另一块屏幕的输入框

**工程**
- 手机端打包脚本：缓存打包 / 全量打包 / 编译校验 / 清理 / 校验 / 安装

## 安装

1. 下载 Release 里的 APK（arm64，Android 10+）；
2. 直接安装即可，**不需要卸载官方版**（包名不同）；
3. 首次打开是全新数据；账号需要重新登录一次。

> 注意：本 APK 为自签名构建，系统可能提示「未知来源」；请确认来源后再安装。

## 编译

源码与上游一致（Kotlin + Gradle），需要 JDK 21、Android SDK（compileSdk 37.2）、build-tools 37。
`local.properties` 写 `sdk.dir=...`，然后：

```bash
./gradlew assembleRelease -Pdsh.fetchShizuku=false
```

产物：`app/build/outputs/apk/release/app-release.apk`

## 合法性说明

- 上游采用 **Apache License 2.0**，允许修改与再分发。本仓库保留原项目的 `LICENSE` 与版权声明，并明确标注为修改版（依据许可证第 4 条：标明修改）。
- 依据 Apache-2.0 第 6 条，**许可证不授予商标使用权**：本项目不使用原作者及 DeepSeek 的名称、标识或商标作官方暗示、背书或商业推广；「DSH」「DeepSeek」等名称及商标归其各自权利人所有。
- 本仓库全部改动均以源码公开，可自行编译核对；不含闭源内容，也不含官方签名。
- 发布的 APK 是**自签名构建**，不是官方更新包。

## 免责声明

- 本改版未经原作者或 DeepSeek 官方审核，稳定性与兼容性不作保证；升级前请自行备份数据。
- 仅供学习研究与个人使用，请勿用于商业用途，也请勿冒充官方发布。
- 若原作者认为本改版不妥，提出后本仓库将立即下线相关内容。

## 致谢

- 感谢 [@jackwener](https://github.com/jackwener) 做出 **DSH-Android**：免 root、免 Termux，让 DeepSeek Harness 真正跑在手机上。没有这个项目，就没有这份改版。
- 感谢上游 [DeepSeek Harness](https://github.com/deepseek-ai/deepseek-harness) 团队把引擎开源。
- 感谢社区里所有提交 issue、PR 和反馈的人。

## 上游与许可

- 上游项目：https://github.com/jackwener/DSH-Android （2026-11-20 归档，只读）
- 引擎：https://github.com/deepseek-ai/deepseek-harness
- 许可证：[Apache License 2.0](LICENSE)
