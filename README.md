# Otherworld Inn HUD

Otherworld Inn HUD 是面向旅社物语整合包维护的 SeasonHud 派生版本。

本仓库基于 [IanMods/SeasonHud](https://github.com/IanMods/SeasonHud)
`active/multi/1.21.1` 分支的
`f0242530dab1057e6f46073ca5d4a5da8527324c` 初始化，并保留 MIT 协议要求的版权与许可文本。

## 这个版本解决了什么

SeasonHud 2.0.6 在 NeoForge / ModLauncher 的并行客户端初始化阶段，可能因为
`ServiceLoader.load(clazz)` 依赖当前线程 context classloader，而找不到自己 jar 内的
`META-INF/services`，从而在启动时抛出类似下面的错误：

```text
Failed to load service for club.iananderson.seasonhud.platform.services.PlatformHelper
```

Otherworld Inn HUD 已将服务加载改为使用服务接口自身的 classloader：

```java
ServiceLoader.load(clazz, clazz.getClassLoader())
```

这避免了线程 context classloader 不稳定导致的随机启动崩溃。

## 保留的 SeasonHud 功能

本 fork 保留 SeasonHud 原有的完整 HUD 功能，包括：

- 季节、日期、子季节显示。
- 肥力信息显示与季节文本替换逻辑。
- HUD 位置、缩放、颜色等客户端配置。
- 小地图集成，包括 Xaero、JourneyMap、FTB Chunks、Map Atlases 等原有兼容逻辑。
- 日历、饰品栏与相关显示条件。
- 原有本地化资源、配置界面和 NeoForge 平台服务结构。

## 旅社物语适配

在保留原功能的基础上，本版本增加了对
[Otherworld Inn](https://github.com/xiwanzi/OtherworldInn) 的 HUD 适配：

- 仅在玩家位于已登记旅社房间内时显示房间信息。
- 在季节 HUD 下方追加房间属性行。
- 显示房间舒适度、光照、湿度、洁净度四项数值。
- 洁净度使用独立图标；数值颜色规则为：
  - `100`：绿色。
  - `1-99`：橙色。
  - `0`：红色。
- 通过运行时反射读取 Otherworld Inn 的客户端房间缓存，不增加硬依赖。
- 如果 Otherworld Inn 未加载或接口变化，房间信息会静默隐藏，不影响季节 HUD 和游戏启动。

## 项目信息

- Mod ID: `otherworldinn_hud`
- 显示名: `Otherworld Inn HUD`
- 作者: `xiwanzi`
- Minecraft: `1.21.1`
- 默认构建平台: `NeoForge`
- Java: `21`

Fabric 和 Forge 源树仍保留在仓库中作为上游结构参考；本整合包当前默认只构建 NeoForge，
`enabled_platforms=neoforge`。

## 构建

```powershell
.\gradlew.bat build
```

NeoForge 运行 jar 会输出到：

```text
neoforge/build/libs/
```

仓库已配置 GitHub Actions，所有 push 和 pull request 都会执行 `./gradlew build`。

## License

SeasonHud 使用 MIT 协议。原始版权声明和 MIT 协议文本保留在 [LICENSE](LICENSE)。
