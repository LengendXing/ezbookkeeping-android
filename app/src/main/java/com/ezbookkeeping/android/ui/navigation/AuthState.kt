package com.ezbookkeeping.android.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthState @Inject constructor() {
    var isLoggedIn by mutableStateOf(false)
    var userId by mutableIntStateOf(1)
    var accessToken by mutableStateOf("")
    var refreshToken by mutableStateOf("")
    var isStandalone by mutableStateOf(true)
    var serverUrl by mutableStateOf("")

    fun loginStandalone() {
        isStandalone = true
        isLoggedIn = true
        userId = 1
    }

    fun loginServer(userId: Int, accessToken: String, refreshToken: String, serverUrl: String) {
        isStandalone = false
        isLoggedIn = true
        this.userId = userId
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.serverUrl = serverUrl
    }

    fun onLogout() {
        isLoggedIn = false
        userId = -1
        accessToken = ""
        refreshToken = ""
    }
}
