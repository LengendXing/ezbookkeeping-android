package com.ezbookkeeping.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ezbookkeeping.android.ui.screen.about.AboutScreen
import com.ezbookkeeping.android.ui.screen.account.AccountEditScreen
import com.ezbookkeeping.android.ui.screen.account.AccountListScreen
import com.ezbookkeeping.android.ui.screen.category.CategoryEditScreen
import com.ezbookkeeping.android.ui.screen.category.CategoryListScreen
import com.ezbookkeeping.android.ui.screen.exchange.ExchangeRateScreen
import com.ezbookkeeping.android.ui.screen.home.HomeScreen
import com.ezbookkeeping.android.ui.screen.login.LoginScreen
import com.ezbookkeeping.android.ui.screen.settings.SettingsScreen
import com.ezbookkeeping.android.ui.screen.statistics.StatisticsScreen
import com.ezbookkeeping.android.ui.screen.tag.TagListScreen
import com.ezbookkeeping.android.ui.screen.tag.TagEditScreen
import com.ezbookkeeping.android.ui.screen.template.TemplateListScreen
import com.ezbookkeeping.android.ui.screen.transaction.TransactionEditScreen
import com.ezbookkeeping.android.ui.screen.transaction.TransactionListScreen
import com.ezbookkeeping.android.ui.screen.signup.SignupScreen
import com.ezbookkeeping.android.ui.screen.unlock.UnlockScreen
import com.ezbookkeeping.android.ui.screen.user.DataManagementScreen
import com.ezbookkeeping.android.ui.screen.user.SessionListScreen
import com.ezbookkeeping.android.ui.screen.user.TwoFactorAuthScreen
import com.ezbookkeeping.android.ui.screen.user.UserProfileScreen
import com.ezbookkeeping.android.ui.screen.settings.ApplicationLockScreen
import com.ezbookkeeping.android.ui.screen.transaction.TransactionImportScreen
import com.ezbookkeeping.android.ui.screen.transaction.CategoryPresetScreen
import com.ezbookkeeping.android.ui.screen.transaction.ExchangeRateUpdateScreen
import com.ezbookkeeping.android.ui.screen.transaction.ReconciliationScreen
import com.ezbookkeeping.android.ui.screen.statistics.InsightExplorerScreen
import com.ezbookkeeping.android.ui.screen.settings.PageSettingsScreen
import com.ezbookkeeping.android.ui.screen.settings.TextSizeSettingsScreen
import com.ezbookkeeping.android.ui.screen.settings.AccountFilterSettingsScreen
import com.ezbookkeeping.android.ui.screen.settings.CategoryFilterSettingsScreen
import com.ezbookkeeping.android.ui.screen.settings.TransactionTagFilterSettingsScreen
import com.ezbookkeeping.android.ui.screen.settings.DisplayOrderSettingsScreen
import com.ezbookkeeping.android.ui.screen.settings.CloudSyncSettingsScreen
import com.ezbookkeeping.android.ui.screen.transaction.AmountFilterScreen
import com.ezbookkeeping.android.ui.screen.account.MoveAllTransactionsScreen
import com.ezbookkeeping.android.ui.screen.statistics.StatisticsSettingsScreen
import com.ezbookkeeping.android.ui.screen.tag.TagGroupListScreen

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val UNLOCK = "unlock"
    const val TRANSACTION_LIST = "transactions"
    const val TRANSACTION_EDIT = "transactions/edit"
    const val TRANSACTION_EDIT_WITH_ID = "transactions/edit/{transactionId}"
    const val ACCOUNT_LIST = "accounts"
    const val ACCOUNT_EDIT = "accounts/edit"
    const val ACCOUNT_EDIT_WITH_ID = "accounts/edit/{accountId}"
    const val CATEGORY_LIST = "categories"
    const val CATEGORY_EDIT = "categories/edit"
    const val CATEGORY_EDIT_WITH_ID = "categories/edit/{categoryId}"
    const val TAG_LIST = "tags"
    const val TAG_EDIT = "tags/edit"
    const val TEMPLATE_LIST = "templates"
    const val STATISTICS = "statistics"
    const val EXCHANGE = "exchange"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val USER_PROFILE = "user/profile"
    const val TWO_FACTOR_AUTH = "user/2fa"
    const val SESSIONS = "user/sessions"
    const val DATA_MANAGEMENT = "user/data"
    const val APPLICATION_LOCK = "settings/app-lock"
    const val TRANSACTION_IMPORT = "transactions/import"
    const val CATEGORY_PRESET = "categories/preset"
    const val EXCHANGE_RATE_UPDATE = "settings/exchange-rate-update"
    const val RECONCILIATION = "transactions/reconciliation"
    const val INSIGHT_EXPLORER = "statistics/insight"
    const val PAGE_SETTINGS = "settings/page-settings"
    const val TEXT_SIZE_SETTINGS = "settings/text-size"
    const val ACCOUNT_FILTER_SETTINGS = "settings/account-filter"
    const val CATEGORY_FILTER_SETTINGS = "settings/category-filter"
    const val TRANSACTION_TAG_FILTER_SETTINGS = "settings/transaction-tag-filter"
    const val DISPLAY_ORDER_SETTINGS = "settings/display-order"
    const val CLOUD_SYNC_SETTINGS = "settings/cloud-sync"
    const val AMOUNT_FILTER = "transactions/amount-filter"
    const val MOVE_ALL_TRANSACTIONS = "accounts/move-all-transactions"
    const val MOVE_ALL_TRANSACTIONS_WITH_ID = "accounts/move-all-transactions/{accountId}"
    const val STATISTICS_SETTINGS = "settings/statistics-settings"
    const val TAG_GROUP_LIST = "tags/groups"
}

data class BottomNavItem(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
fun EZBookkeepingNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomItems = listOf(
        BottomNavItem(Routes.HOME, "Details") { Icon(Icons.Default.Home, contentDescription = "Details") },
        BottomNavItem(Routes.ACCOUNT_LIST, "Accounts") { Icon(Icons.Default.List, contentDescription = "Accounts") },
        BottomNavItem(Routes.TRANSACTION_EDIT, "Add") { Icon(Icons.Default.Add, contentDescription = "Add") },
        BottomNavItem(Routes.STATISTICS, "Statistics") { Icon(Icons.Default.PieChart, contentDescription = "Statistics") },
        BottomNavItem(Routes.SETTINGS, "Settings") { Icon(Icons.Default.Settings, contentDescription = "Settings") }
    )
    val bottomRoutes = bottomItems.map { it.route }.toSet()
    val showBottomBar = currentDestination?.route in bottomRoutes

    Scaffold(bottomBar = {
        if (showBottomBar) {
            NavigationBar {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        icon = item.icon, label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = { navController.navigate(item.route) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } }
                    )
                }
            }
        }
    }) { innerPadding ->
        NavHost(navController = navController, startDestination = Routes.LOGIN, modifier = Modifier.padding(innerPadding)) {
            composable(Routes.LOGIN) { LoginScreen(navController) }
            composable(Routes.SIGNUP) { SignupScreen(navController) }
            composable(Routes.UNLOCK) { UnlockScreen(navController) }
            composable(Routes.HOME) { HomeScreen(navController) }
            composable(Routes.TRANSACTION_LIST) { TransactionListScreen(navController) }
            composable(Routes.TRANSACTION_EDIT) { TransactionEditScreen(navController) }
            composable(Routes.TRANSACTION_EDIT_WITH_ID, arguments = listOf(navArgument("transactionId") { type = NavType.IntType })) { TransactionEditScreen(navController, it.arguments?.getInt("transactionId")) }
            composable(Routes.ACCOUNT_LIST) { AccountListScreen(navController) }
            composable(Routes.ACCOUNT_EDIT) { AccountEditScreen(navController) }
            composable(Routes.ACCOUNT_EDIT_WITH_ID, arguments = listOf(navArgument("accountId") { type = NavType.IntType })) { AccountEditScreen(navController, it.arguments?.getInt("accountId")) }
            composable(Routes.CATEGORY_LIST) { CategoryListScreen(navController) }
            composable(Routes.CATEGORY_EDIT) { CategoryEditScreen(navController) }
            composable(Routes.CATEGORY_EDIT_WITH_ID, arguments = listOf(navArgument("categoryId") { type = NavType.IntType })) { CategoryEditScreen(navController, it.arguments?.getInt("categoryId")) }
            composable(Routes.TAG_LIST) { TagListScreen(navController) }
            composable(Routes.TAG_EDIT) { TagEditScreen(navController) }
            composable(Routes.TEMPLATE_LIST) { TemplateListScreen(navController) }
            composable(Routes.STATISTICS) { StatisticsScreen(navController) }
            composable(Routes.EXCHANGE) { ExchangeRateScreen(navController) }
            composable(Routes.SETTINGS) { SettingsScreen(navController) }
            composable(Routes.ABOUT) { AboutScreen(navController) }
            composable(Routes.USER_PROFILE) { UserProfileScreen(navController) }
            composable(Routes.TWO_FACTOR_AUTH) { TwoFactorAuthScreen(navController) }
            composable(Routes.SESSIONS) { SessionListScreen(navController) }
            composable(Routes.DATA_MANAGEMENT) { DataManagementScreen(navController) }
            composable(Routes.APPLICATION_LOCK) { ApplicationLockScreen(navController) }
            composable(Routes.TRANSACTION_IMPORT) { TransactionImportScreen(navController) }
            composable(Routes.CATEGORY_PRESET) { CategoryPresetScreen(navController) }
            composable(Routes.EXCHANGE_RATE_UPDATE) { ExchangeRateUpdateScreen(navController) }
            composable(Routes.RECONCILIATION) { ReconciliationScreen(navController) }
            composable(Routes.INSIGHT_EXPLORER) { InsightExplorerScreen(navController) }
            composable(Routes.PAGE_SETTINGS) { PageSettingsScreen(navController) }
            composable(Routes.TEXT_SIZE_SETTINGS) { TextSizeSettingsScreen(navController) }
            composable(Routes.ACCOUNT_FILTER_SETTINGS) { AccountFilterSettingsScreen(navController) }
            composable(Routes.CATEGORY_FILTER_SETTINGS) { CategoryFilterSettingsScreen(navController) }
            composable(Routes.TRANSACTION_TAG_FILTER_SETTINGS) { TransactionTagFilterSettingsScreen(navController) }
            composable(Routes.DISPLAY_ORDER_SETTINGS) { DisplayOrderSettingsScreen(navController) }
            composable(Routes.CLOUD_SYNC_SETTINGS) { CloudSyncSettingsScreen(navController) }
            composable(Routes.AMOUNT_FILTER) { AmountFilterScreen(navController) }
            composable(Routes.MOVE_ALL_TRANSACTIONS) { MoveAllTransactionsScreen(navController) }
            composable(Routes.MOVE_ALL_TRANSACTIONS_WITH_ID, arguments = listOf(navArgument("accountId") { type = NavType.IntType })) { MoveAllTransactionsScreen(navController, it.arguments?.getInt("accountId")) }
            composable(Routes.STATISTICS_SETTINGS) { StatisticsSettingsScreen(navController) }
            composable(Routes.TAG_GROUP_LIST) { TagGroupListScreen(navController) }
        }
    }
}
