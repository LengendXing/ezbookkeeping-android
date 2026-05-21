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

## Phase 5 - P1/P2页面 ✅ 2026-05-21
- [x] TransactionImportScreen (CSV/OFX/QIF导入) ✅
- [x] CategoryPresetScreen (预设分类导入) ✅
- [x] ExchangeRateUpdateScreen (汇率更新设置) ✅
- [x] ReconciliationScreen (对账单) ✅
- [x] InsightExplorerScreen (数据洞察) ✅
- [x] PageSettingsScreen (页面设置) ✅
- [x] TextSizeSettingsScreen (字体大小) ✅
- [x] AccountFilterSettingsScreen (账户筛选) ✅
- [x] CategoryFilterSettingsScreen (分类筛选) ✅
- [x] TransactionTagFilterSettingsScreen (标签筛选) ✅
- [x] DisplayOrderSettingsScreen (显示顺序) ✅
- [x] CloudSyncSettingsScreen (云同步) ✅
- [x] 305个单元测试全部通过 ✅

---

## Phase 6 - 核心交互补齐 (v0.6.0) ✅ 2026-05-21
> 目标：补齐Web端核心交互组件和页面内深度功能

### 6.1 通用交互组件 (P0)
- [x] NumberPadSheet ✅ — 自定义数字键盘金额输入
- [x] TreeViewSelectionSheet ✅ — 分类树形选择(带搜索过滤)
- [x] TwoColumnListItemSelectionSheet ✅ — 账户按类型分组选择
- [x] DateSelectionSheet ✅ — 日期选择器
- [x] DateTimeSelectionSheet ✅ — 日期+时间选择器
- [x] DateRangeSelectionSheet ✅ — 日期范围选择器
- [x] ColorSelectionSheet ✅ — 颜色选择器
- [x] IconSelectionSheet ✅ — 图标选择器
- [x] TransactionTagSelectionSheet ✅ — 标签多选
- [x] ListItemSelectionSheet ✅ — 通用列表单项选择
- [x] PinCodeInputSheet ✅ — PIN码输入
- [x] PasswordInputSheet ✅ — 密码确认输入

### 6.2 HomeScreen 重写
- [x] ✅ 今日/本周/本月/本年概览卡片(4行带日期+收支金额)
- [x] ✅ 月份概览卡片(黄底大字月支出+收入小字)
- [x] ✅ 隐私模式eye图标切换金额显示
- [x] ✅ 下拉刷新 (pull-to-refresh)
- [x] ✅ 长按+号弹出模板列表popover

### 6.3 TransactionEditScreen 补齐
- [x] ✅ ModifyBalance交易类型(第4种)
- [x] ✅ 数字键盘金额输入(替换TextField)
- [x] ✅ 分类树形选择+搜索过滤
- [x] ✅ 账户按类型分组选择(支出/收入/负债分组)
- [x] ✅ 日期+时间选择器(含时区)
- [x] ✅ 标签多选
- [x] ✅ 更多操作菜单(复制/存为模板)

### 6.4 TransactionListScreen 补齐
- [x] ✅ 搜索栏(SearchBar) keyword搜索
- [x] ✅ 下拉刷新 (pull-to-refresh)
- [x] ✅ 无限滚动加载更多 (infinite scroll)
- [x] ✅ 导入按钮(navbar)
- [x] ✅ 账户筛选(accountIds多选)
- [x] ✅ 分类筛选(categoryIds多选)
- [x] ✅ 标签筛选(tagFilter)
- [x] ✅ 金额范围筛选(AmountFilterPage)
- [x] ✅ 自定义日期范围(CustomDateRangeSheet)
- [x] ✅ 自定义月份(CustomMonthSheet)

### 6.5 新增页面
- [x] ✅ AmountFilterPage — 交易金额范围筛选页
- [x] ✅ MoveAllTransactionsPage — 移动全部交易到其他账户
- [x] ✅ StatisticsSettingsPage — 统计设置页

---

## Phase 7 - 功能补齐 (v0.7.0) ✅ 2026-05-21
> 目标：补齐现有页面的深度交互功能

### 7.1 AccountListScreen 补齐
- [x] 移动全部交易到其他账户(MoveAllTransactionsPage) ✅
- [x] 清空账户交易(clearAllTransactions) ✅
- [x] 单账户对账入口(per-account reconciliation link) ✅
- [x] 危险操作密码确认(PasswordInputSheet) ✅

### 7.2 CategoryListScreen 补齐
- [x] 预设分类入口链接(导航到PresetPage) ✅
- [x] 分类隐藏/显示切换(hidden toggle) ✅
- [x] 分类更多操作菜单(hide/show/delete) ✅

### 7.3 StatisticsScreen 补齐
- [x] 图表类型切换(Pie/Bar/Radar/Trend 4种) ✅
- [x] 数据类型选择(Expense/Income/Both) ✅
- [x] 日期聚合方式(By Day/Week/Month/Year) ✅
- [x] 排序方式(By Amount/Amount Desc) ✅
- [x] 统计设置页(StatisticsSettingsPage) ✅

### 7.4 TransactionEditScreen 定期交易
- [x] ScheduleFrequencySheet — 定期交易频率选择 ✅
- [x] 定期交易起止日期(start/end date) ✅

### 7.5 日期/时间组件
- [x] MonthSelectionSheet — 月份选择 ✅
- [x] MonthRangeSelectionSheet — 月份范围选择 ✅
- [x] FiscalYearStartSelectionSheet — 财年起始月选择 ✅

### 7.6 ReconciliationScreen 补齐
- [x] 期初余额/期末余额(opening/closing balance) ✅
- [x] 按账户对账(per-account entry) ✅
- [x] 调整余额交易(ModifyBalance集成) ✅

### 7.7 TagListScreen 补齐
- [x] 标签分组独立管理页(GroupListPage) ✅
- [x] 标签分组显示顺序调整(TagGroupChangeDisplayOrderDialog) ✅

### 7.8 SettingsScreen 补齐
- [x] 定期交易管理入口(Scheduled Transactions) ✅
- [x] 统计设置入口(Statistics Settings) ✅
- [x] 主题选择器(多主题，非仅暗/亮) ✅
- [x] 启用滑动返回(Enable Swipe Back) ✅

---

## Phase 8 - 扩展功能 (v0.8.0) 🔲
> 目标：高级功能和扩展组件

### 8.1 TransactionEditScreen 高级功能
- [ ] 地理位置选择(MapSheet)
- [ ] 图片附件(Pictures上传)
- [ ] AI图片识别(AIImageRecognitionSheet)
- [ ] 语音记账(VoiceTransactionSheet)

### 8.2 TransactionImportScreen 多步向导
- [ ] 多步向导(上传→定义列→检查→执行)
- [ ] 自定义脚本执行(ExecuteCustomScriptTab)
- [ ] 批量创建/替换操作(BatchCreate/BatchReplace)
- [ ] 列映射配置(DefineColumnTab)

### 8.3 InsightExplorerScreen 补齐
- [ ] 自定义查询构建器(QueryTab)
- [ ] 数据表格视图(DataTableTab)
- [ ] 显示顺序调整(DisplayOrderDialog)

### 8.4 认证流程补齐
- [ ] ForgetPasswordPage — 忘记密码
- [ ] ResetPasswordPage — 重置密码
- [ ] VerifyEmailPage — 邮箱验证
- [ ] OAuth2CallbackPage — OAuth2回调

### 8.5 剩余P2组件
- [ ] MapSheet — 地图位置选择
- [ ] InformationSheet — 信息展示
- [ ] ListItemSelectionPopup — 通用弹出选择
- [ ] ItemIcon — 图标显示组件
- [ ] LanguageSelectButton — 语言切换
- [ ] TrendsBarChart — 趋势柱状图
- [ ] AccountBalanceTrendsBarChart — 账户余额趋势图
- [ ] ListNumberInput — 列表内数字输入

---

## Phase 9 - 优化发布 🔲
- [ ] 测试用例扩充至每个功能80+用例
- [ ] 无障碍支持优化
- [ ] 性能 profiling
- [ ] 代码审查与重构

---

## 缺失清单统计 (Web→Android全量对照)

| 类别 | 数量 | 状态 |
|------|------|------|
| 缺失页面 | 4个 | AmountFilterPage, MoveAllTransactionsPage, StatisticsSettingsPage, TagGroupListPage |
| 页面内缺失功能 | 58项 | 分布在Home/TransactionList/TransactionEdit/Account/Category/Statistics/Insight/Import/Reconciliation/Tag/Settings |
| 缺失通用组件 | 28个 | NumberPad/TreeView/TwoColumn/Date系列/MapSheet/AI/Voice等 |
| 缺失认证流程 | 3个 | ForgetPassword/ResetPassword/VerifyEmail |
| **总计缺失** | **93项** | |
