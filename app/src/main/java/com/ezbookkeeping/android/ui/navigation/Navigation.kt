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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ezbookkeeping.android.ui.screen.login.LoginScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.home.HomeScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.transaction.TransactionListScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.transaction.TransactionEditScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.account.AccountListScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.account.AccountEditScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.category.CategoryListScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.category.CategoryEditScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.tag.TagListScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.tag.TagEditScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.template.TemplateListScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.statistics.StatisticsScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.exchange.ExchangeRateScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.settings.SettingsScreenPlaceholder
import com.ezbookkeeping.android.ui.screen.about.AboutScreenPlaceholder

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val TRANSACTION_LIST = "transactions"
    const val TRANSACTION_EDIT = "transactions/edit"
    const val ACCOUNT_LIST = "accounts"
    const val ACCOUNT_EDIT = "accounts/edit"
    const val CATEGORY_LIST = "categories"
    const val CATEGORY_EDIT = "categories/edit"
    const val TAG_LIST = "tags"
    const val TAG_EDIT = "tags/edit"
    const val TEMPLATE_LIST = "templates"
    const val STATISTICS = "statistics"
    const val EXCHANGE = "exchange"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

data class BottomNavItem(val route: String, val label: String, val icon: @Composable () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EZBookkeepingNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomItems = listOf(
        BottomNavItem(Routes.HOME, "Home") {
            Icon(Icons.Default.Home, contentDescription = "Home")
        },
        BottomNavItem(Routes.TRANSACTION_LIST, "Transactions") {
            Icon(Icons.Default.List, contentDescription = "Transactions")
        },
        BottomNavItem(Routes.STATISTICS, "Statistics") {
            Icon(Icons.Default.PieChart, contentDescription = "Statistics")
        },
        BottomNavItem(Routes.SETTINGS, "Settings") {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    )

    val showBottomBar = bottomItems.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            icon = item.icon,
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) { LoginScreenPlaceholder() }
            composable(Routes.HOME) { HomeScreenPlaceholder() }
            composable(Routes.TRANSACTION_LIST) { TransactionListScreenPlaceholder() }
            composable(Routes.TRANSACTION_EDIT) { TransactionEditScreenPlaceholder() }
            composable(Routes.ACCOUNT_LIST) { AccountListScreenPlaceholder() }
            composable(Routes.ACCOUNT_EDIT) { AccountEditScreenPlaceholder() }
            composable(Routes.CATEGORY_LIST) { CategoryListScreenPlaceholder() }
            composable(Routes.CATEGORY_EDIT) { CategoryEditScreenPlaceholder() }
            composable(Routes.TAG_LIST) { TagListScreenPlaceholder() }
            composable(Routes.TAG_EDIT) { TagEditScreenPlaceholder() }
            composable(Routes.TEMPLATE_LIST) { TemplateListScreenPlaceholder() }
            composable(Routes.STATISTICS) { StatisticsScreenPlaceholder() }
            composable(Routes.EXCHANGE) { ExchangeRateScreenPlaceholder() }
            composable(Routes.SETTINGS) { SettingsScreenPlaceholder() }
            composable(Routes.ABOUT) { AboutScreenPlaceholder() }
        }
    }
}
