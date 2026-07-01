# Otherworld Inn HUD

Otherworld Inn HUD 是为「旅社物语」整合环境维护的 HUD 扩展模组，基于
[SeasonHud](https://github.com/IanMods/SeasonHud) 的 `active/multi/1.21.1` 分支派生。

它保留 SeasonHud 的季节显示、日期显示、小地图集成和日历逻辑，并额外适配
[Otherworld Inn](https://github.com/Yourzi/OtherworldInn) 的房间状态与委托 HUD。

当前仓库默认构建目标为 **Minecraft 1.21.1 + Java 21 + NeoForge**。
源码中仍保留 Fabric / Forge 多加载器结构，但 `gradle.properties` 目前只启用 `neoforge`。

## 功能

- 显示当前季节、子季节、季节日期和季节图标。
- 支持季节名称按春、夏、秋、冬、雨季、旱季分别着色。
- 支持在热带季节、生物群系肥沃度等兼容数据存在时显示额外季节信息。
- 支持在无小地图时显示独立 HUD，也可以集成到常见小地图模组的信息区域。
- 可选要求玩家携带日历物品后才显示季节 HUD。
- 适配 Otherworld Inn 房间状态，在玩家位于已登记旅社房间内时显示房间属性。
- 适配 Otherworld Inn 委托 HUD，按住 `Tab` 时显示当前旅客心愿和小镇委托。

## Otherworld Inn 适配

安装 Otherworld Inn 后，本模组会尝试读取旅社和委托数据。

房间 HUD 会显示：

- 舒适度
- 光照
- 湿度
- 洁净度

洁净度会按数值显示不同颜色：

- `100`：绿色
- `1-99`：橙色
- `0`：红色

委托 HUD 会显示：

- 旅客心愿
- 小镇委托
- 任务标题和旅客名称
- 物品或击杀等需求进度
- 奖励内容
- 完成状态

委托 HUD 默认启用，默认位置为屏幕居中。游戏内按住 `Tab` 时显示，最多显示两个当前任务。
任务完成或消失后会淡出；没有 Otherworld Inn、没有队伍数据或没有任务快照时不会显示。

Otherworld Inn 适配是软适配：如果没有安装 Otherworld Inn，或接口不可用，相关 HUD 会自动隐藏，不影响季节 HUD 和游戏启动。

## 支持的兼容项

季节模组兼容代码包括：

- Serene Seasons
- Fabric Seasons / Fabric Seasons Extras
- TerraFirmaCraft
- Ecliptic Seasons
- Homeostatic Seasons
- ProtoManly's Weather

小地图兼容代码包括：

- Xaero's Minimap
- Xaero's Minimap Fair-play
- Xaero's Minimap Better PVP
- JourneyMap
- FTB Chunks
- Map Atlases

日历饰品槽兼容代码包括：

- Curios
- Trinkets
- Accessories

当前默认发布目标是 NeoForge，因此实际可用兼容项以对应加载器和整合包安装情况为准。

## 配置

NeoForge 环境下可以从模组列表打开配置界面，也可以直接编辑配置文件。

客户端配置：

```text
config/otherworldinn_hud-client.toml
```

服务器配置：

```text
<world>/serverconfig/otherworldinn_hud-server.toml
```

常用客户端配置包括：

- 是否启用模组。
- 季节 HUD 的位置、缩放和自定义偏移。
- 是否显示子季节、日期、热带季节、肥沃度。
- 季节名称颜色。
- 是否启用小地图集成。
- 小地图隐藏时是否回退显示默认 HUD。
- 是否启用委托 HUD。
- 委托 HUD 位置、缩放、右侧边距、垂直偏移、小地图预留高度和任务间距。

常用服务器配置包括：

- 是否需要日历才显示季节 HUD。
- 是否启用日历详细模式。
- Fabric Seasons 日长修正值。
- Serene Seasons 子季节长度修正值。

## 安装

推荐环境：

- Minecraft `1.21.1`
- Java `21`
- NeoForge `21.1.x`
- Otherworld Inn 当前开发版本

将构建出的 `otherworldinn_hud` jar 放入客户端 `mods` 目录即可。
如果服务端也安装本模组，服务端配置会控制日历相关规则。

## 开发与构建

当前仓库默认只启用 NeoForge 平台：

```properties
enabled_platforms=neoforge
```

编译：

```powershell
.\gradlew.bat :neoforge:compileJava
```

构建 jar：

```powershell
.\gradlew.bat :neoforge:build
```

产物位于：

```text
neoforge/build/libs/
```

本项目需要 Java 21。若本机默认 Java 不是 21，请先设置 `JAVA_HOME` 和 `Path`。

## 与原 SeasonHud 的差异

这个派生版本主要增加了旅社物语整合包所需的稳定性和 Otherworld Inn 专属 HUD：

- 使用服务接口自身的 classloader 加载 `META-INF/services`，降低 NeoForge / ModLauncher 并行初始化时的服务加载失败风险。
- NeoForge 客户端初始化和 HUD layer 注册显式订阅 MOD event bus。
- 新增 Otherworld Inn 房间属性 HUD。
- 新增 Otherworld Inn 旅客心愿 / 小镇委托 HUD。
- 保留原 SeasonHud 的季节、小地图、日历和饰品槽兼容逻辑。

## License

本项目沿用 SeasonHud 的 MIT 协议。

原始版权声明和协议文本见 [LICENSE](LICENSE)。
