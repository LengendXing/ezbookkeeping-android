# ezBookkeeping Android - 变更日志

## v0.6.0 - 2026-05-21
### 变更内容
- Phase 6 核心交互补齐：12个通用组件 + 3个屏幕增强 + 3个新页面
### 影响范围
- ui/component/* (12个新组件: NumberPadSheet, TreeViewSelectionSheet, TwoColumnListItemSelectionSheet, DateSelectionSheet, DateTimeSelectionSheet, DateRangeSelectionSheet, ColorSelectionSheet, IconSelectionSheet, TransactionTagSelectionSheet, ListItemSelectionSheet, PinCodeInputSheet, PasswordInputSheet)
- ui/screen/home/* (HomeScreen 重写: pull-to-refresh, 长按FAB弹出模板, 隐私模式eye图标)
- ui/screen/transaction/TransactionEditScreen (4种交易类型+NumberPad+TreeView选择+标签多选+更多操作菜单)
- ui/screen/transaction/TransactionListScreen (搜索+pull-to-refresh+无限滚动+账户/分类/标签/日期筛选)
- ui/screen/transaction/AmountFilterScreen (新增)
- ui/screen/account/MoveAllTransactionsScreen (新增)
- ui/screen/statistics/StatisticsSettingsScreen (新增)
- ui/navigation/Navigation.kt (3新路由)
### 功能列表
- NumberPadSheet: 自定义数字键盘金额输入(支持小数点+退格+确认)
- TreeViewSelectionSheet: 分类树形选择(带搜索过滤+层级缩进)
- TwoColumnListItemSelectionSheet: 账户按类型分组选择
- DateSelectionSheet: 日历式日期选择器(月份导航+今日高亮)
- DateTimeSelectionSheet: 日期+时间选择器(小时+分钟滚动列表)
- DateRangeSelectionSheet: 日期范围选择器(快速范围: 本周/本月/本年)
- ColorSelectionSheet: 颜色选择器(30色调色板+预览)
- IconSelectionSheet: 图标选择器(46个Material图标网格)
- TransactionTagSelectionSheet: 标签多选(按分组显示+checkbox+全选清除)
- ListItemSelectionSheet: 通用列表单项选择
- PinCodeInputSheet: PIN码输入(4位圆点指示器+数字键盘)
- PasswordInputSheet: 密码确认输入(显示/隐藏切换+错误提示)
- HomeScreen: PullToRefreshBox下拉刷新, ExperimentalFoundationApi长按FAB弹出模板DropdownMenu
- TransactionEditScreen: ModifyBalance第4种交易类型, NumberPadSheet金额输入, TreeViewSelectionSheet分类选择, TwoColumnListItemSelectionSheet账户选择, DateTimeSelectionSheet日期时间, TransactionTagSelectionSheet标签多选, 更多操作菜单(复制/存为模板)
- TransactionListScreen: SearchBar搜索, PullToRefreshBox下拉刷新, 无限滚动加载更多, 导入按钮, 账户/分类/标签/日期范围筛选, DateRangeSelectionSheet
- AmountFilterScreen: 金额范围筛选(最小/最大+快速预设<100/100-1k/>1k)
- MoveAllTransactionsScreen: 移动全部交易到其他账户(密码确认+危险操作警告)
- StatisticsSettingsScreen: 统计设置(图表类型4种/数据类型3种/聚合方式4种/排序2种/子分类显示/其他分类阈值)
- Navigation: AMOUNT_FILTER, MOVE_ALL_TRANSACTIONS, MOVE_ALL_TRANSACTIONS_WITH_ID, STATISTICS_SETTINGS 路由

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
