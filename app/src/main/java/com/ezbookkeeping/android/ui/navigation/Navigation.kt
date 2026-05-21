package com.ezbookkeeping.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import com.ezbookkeeping.android.ui.screen.template.TemplateListScreen
import com.ezbookkeeping.android.ui.screen.transaction.TransactionEditScreen
import com.ezbookkeeping.android.ui.screen.transaction.TransactionListScreen
import com.ezbookkeeping.android.ui.screen.signup.SignupScreen
import com.ezbookkeeping.android.ui.screen.unlock.UnlockScreen

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
}

data class BottomNavItem(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
fun EZBookkeepingNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomItems = listOf(
        BottomNavItem(Routes.HOME, "Home") { Icon(Icons.Default.Home, contentDescription = "Home") },
        BottomNavItem(Routes.TRANSACTION_LIST, "Transactions") { Icon(Icons.Default.List, contentDescription = "Transactions") },
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
            composable(Routes.TEMPLATE_LIST) { TemplateListScreen(navController) }
            composable(Routes.STATISTICS) { StatisticsScreen(navController) }
            composable(Routes.EXCHANGE) { ExchangeRateScreen(navController) }
            composable(Routes.SETTINGS) { SettingsScreen(navController) }
            composable(Routes.ABOUT) { AboutScreen(navController) }
        }
    }
}
