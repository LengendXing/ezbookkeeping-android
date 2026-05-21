# ezBookkeeping Android - 开发计划

## Phase 1 - 项目骨架搭建 ✅ 2026-05-21
- [x] 初始化 Git 仓库 + 远程推送 ✅
- [x] app/build.gradle.kts + 依赖配置 ✅
- [x] 14 个 Room Entity ✅
- [x] 11 个 Room DAO + AppDatabase + TypeConverter ✅
- [x] Hilt DI 模块 ✅
- [x] 7 个 Repository ✅
- [x] UI 骨架 (Theme/Navigation/15 Screen) ✅
- [x] Retrofit API + DTO ✅
- [x] lint/build 验证 ✅

## Phase 2 - 核心功能 ✅ 2026-05-21
- [x] Login/Signup/Unlock 页面 ✅
- [x] Home 首页交易列表 ✅
- [x] Transaction 新增/编辑 ✅
- [x] Account 管理页面 ✅
- [x] Category 管理页面 ✅
- [x] AuthState + AuthInterceptor ✅
- [x] UserPreferences DataStore ✅

## Phase 3 - 扩展功能 ✅ 2026-05-21
- [x] 标签管理页面 ✅
- [x] 模板管理页面 ✅
- [x] 统计图表页面 ✅
- [x] 汇率管理页面 ✅
- [x] 设置页面完整实现 ✅
- [x] About 页面 ✅

## Phase 4 - 高级功能 ✅ 2026-05-21
- [x] TOTP 二次验证服务 ✅
- [x] 应用锁 (PIN/生物识别) ✅
- [x] i18n 多语言 (EN + ZH) ✅

## Phase 5 - 优化发布 ✅ 2026-05-21
- [x] 全部 Phase 编译通过 ✅
- [x] 版本号维护 ✅
- [x] maintain.md / plan.md 更新 ✅

## 后续迭代（未来会话）
- [ ] 测试用例继续扩充（目标每个功能80+用例，当前总计215）
- [ ] TransactionImportPage (P1) CSV/Excel导入交易
- [ ] CategoryPresetPage (P1) 预设分类导入
- [ ] ExchangeRateUpdatePage (P1) 汇率更新设置
- [ ] ReconciliationStatementPage (P1) 对账单
- [ ] InsightExplorerScreen (P1) 数据洞察/趋势分析
- [ ] PageSettingsPage (P2) 页面显示设置
- [ ] TextSizeSettingsPage (P2) 字体大小设置
- [ ] 各种FilterSettingsPage (P2) 筛选设置
- [ ] CloudSyncSettingsPage (P2) 云同步设置
- [ ] 无障碍支持优化
- [ ] 语音记账
- [ ] 性能 profiling
