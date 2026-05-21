# ezBookkeeping Android - 变更日志

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
