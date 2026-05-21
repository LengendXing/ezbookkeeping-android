package com.ezbookkeeping.android.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthState @Inject constructor() {
    var isLoggedIn by mutableStateOf(false)
    var userId by mutableStateOf(-1)
    var accessToken by mutableStateOf("")
    var refreshToken by mutableStateOf("")

    fun onLogin(userId: Int, accessToken: String, refreshToken: String) {
        this.isLoggedIn = true
        this.userId = userId
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun onLogout() {
        isLoggedIn = false
        userId = -1
        accessToken = ""
        refreshToken = ""
    }
}
