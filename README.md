# Otherworld Inn HUD

Otherworld Inn HUD 是 [SeasonHud](https://github.com/IanMods/SeasonHud) 的一个小型派生版本，基于 `active/multi/1.21.1` 分支维护。

这个版本主要给旅社物语整合包使用，保留 SeasonHud 原本功能，同时做了两处调整：

1. 修复 NeoForge 环境下偶发的服务加载崩溃
2. 增加 [Otherworld Inn](https://github.com/xiwanzi/OtherworldInn) 的房间 HUD 显示

## 为什么会有这个版本

SeasonHud 2.0.6 在 NeoForge / ModLauncher 的客户端启动阶段，偶尔会因为服务加载失败导致游戏崩溃，例如：

```text
Failed to load service for club.iananderson.seasonhud.platform.services.PlatformHelper
```

问题大概出在 `ServiceLoader.load(clazz)` 依赖当前线程的 context classloader。
在并行初始化过程中，这个 classloader 有时找不到 mod jar 里的 `META-INF/services` 文件。

这个版本把服务加载方式改成了使用服务接口自己的 classloader：

```java
ServiceLoader.load(clazz, clazz.getClassLoader())
```

这样可以避开线程 context classloader 不稳定带来的启动问题。

## Otherworld Inn 适配

如果安装了 [Otherworld Inn](https://github.com/xiwanzi/OtherworldInn)，HUD 会在季节信息下方额外显示当前房间状态。

房间信息只会在玩家位于已登记的旅社房间内时显示，包括：

* 舒适度
* 光照
* 湿度
* 洁净度

洁净度使用独立图标，并按数值显示不同颜色：

* `100`：绿色
* `1-99`：橙色
* `0`：红色

如果没有安装 Otherworld Inn，或者接口发生变化，房间信息会自动隐藏，不会影响原本的季节 HUD，也不会影响游戏启动。

## License

SeasonHud 使用 MIT 协议。

原始版权声明和协议文本保留在 [LICENSE](LICENSE)。
