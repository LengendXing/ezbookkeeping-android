# ezBookkeeping Android - 变更日志

## v0.5.0 - 2026-05-21
### 变更内容
- P1+P2 全部页面实现 + 90 新增测试用例
### 影响范围
- ui/screen/transaction/* (TransactionImport, CategoryPreset, ExchangeRateUpdate, Reconciliation 新增)
- ui/screen/statistics/* (InsightExplorer 新增)
- ui/screen/settings/* (PageSettings, TextSize, AccountFilter, CategoryFilter, TransactionTagFilter, DisplayOrder, CloudSync 新增)
- ui/navigation/Navigation.kt (13 新路由)
- ui/screen/settings/SettingsScreen.kt (9 新导航入口)
- app/src/test/* (NewFeaturesTest 90个测试方法)
### 功能列表
- TransactionImportScreen: CSV/OFX/QIF格式选择, 文件选择, 预览列表, 确认导入
- CategoryPresetScreen: 预设分类选择(Checkbox), 批量导入
- ExchangeRateUpdateScreen: 自动更新开关, 频率选择(Daily/Weekly/Monthly), 来源选择(ECB/FRB/BoE), 手动更新
- ReconciliationScreen: 对账余额汇总, 交易匹配/不匹配标记, 差异计算, 确认对账
- InsightExplorerScreen: 周期选择(Weekly/Monthly/Yearly), 储蓄率卡片, 收支趋势图, Top分类进度条
- PageSettingsScreen: 默认落地页, 概览卡片/金额/备注显示开关, 分页大小
- TextSizeSettingsScreen: 字体缩放滑块(80%-150%), 实时预览
- AccountFilterSettingsScreen: 账户显示筛选, 全选/取消全选
- CategoryFilterSettingsScreen: 分类显示筛选(支出/收入分组), 全选/取消
- TransactionTagFilterSettingsScreen: 标签筛选, 全选/取消
- DisplayOrderSettingsScreen: 账户/分类排序(上下箭头)
- CloudSyncSettingsScreen: WebDAV/Dropbox/GoogleDrive选择, 自动同步, Wi-Fi限制, 手动同步
- Navigation: 13新路由注册(TRANSACTION_IMPORT, CATEGORY_PRESET, EXCHANGE_RATE_UPDATE, RECONCILIATION, INSIGHT_EXPLORER, PAGE/TEXT_SIZE/ACCOUNT_FILTER/CATEGORY_FILTER/TAG_FILTER/DISPLAY_ORDER/CLOUD_SYNC_SETTINGS)
- SettingsScreen: 9个新导航入口(导入交易/预设分类/对账/汇率更新/页面设置/字体大小/账户筛选/分类筛选/标签筛选/显示顺序/数据洞察/云同步)
- 90 新增单元测试: UiState默认值, 枚举值, 数据类创建, 业务逻辑验证

## v0.4.0 - 2026-05-21
### 变更内容
- P0 页面全面重写 + ApplicationLockPage + 215 单元测试
### 影响范围
- ui/screen/* (Account/Category/Tag/Statistics/Template 重写)
- ui/screen/settings/ApplicationLockScreen (新增)
- ui/navigation/Navigation.kt (新增路由)
- data/local/UserPreferences.kt (lockType/lockCode)
- util/DateUtil.kt (lastMonthStart/lastMonthEnd)
- app/src/test/* (4个测试文件, 215个测试方法)
### 功能列表
- AccountListScreen: 概览卡片(净资产/总资产/总负债), 按类型分组, 余额显示/隐藏, 长按删除
- CategoryListScreen: 类型FilterChip, 父子层级展示, 删除确认
- TagListScreen: 标签分组管理, 新建分组对话框, 标签CRUD
- StatisticsScreen: 日期范围选择器, 饼图, 分类统计明细+进度条
- TemplateListScreen: 快速创建交易, 类型着色, 长按删除
- ApplicationLockScreen: PIN/密码/生物识别设置, 确认输入, 移除锁
- Navigation: TAG_EDIT + APPLICATION_LOCK 路由注册
- SettingsScreen: 4个导航TODO修复, AppLock路由更新
- UserPreferences: lockType Flow + setLockType/setLockCode 持久化
- 215 单元测试: AmountUtil(30), DateUtil(31), Enums(46), UiState(42), ViewModelLogic(66)

## v0.3.0 - 2026-05-21
### 变更内容
- Phase 4: TOTP 服务、应用锁、i18n 多语言
### 影响范围
- service/, res/values*/
### 功能列表
- TotpService: 生成/验证 TOTP 码
- AppLockService: 生物识别 + PIN 锁
- i18n: English + 简体中文 (60+ keys)

## v0.2.0 - 2026-05-21
### 变更内容
- Phase 2+3: 核心UI + 扩展功能
### 影响范围
- ui/screen/* (全部15个Screen完整实现)
- 新增 10 个 ViewModel
### 功能列表
- Login/Signup/Unlock 完整表单
- Home 首页交易列表 + 收支汇总
- Transaction/Account/Category 新增编辑
- Tag/Template/Statistics/Exchange/Settings/About

## v0.1.0 - 2026-05-21
### 变更内容
- Phase 1: 项目骨架搭建
### 影响范围
- 全项目初始化
### 功能列表
- 14 Room Entity + 11 DAO + AppDatabase
- Hilt DI + Repository + Retrofit API
- UI 骨架 + Theme + Navigation
