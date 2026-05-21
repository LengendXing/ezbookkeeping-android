package com.ezbookkeeping.android.ui.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CloudSyncSettingsUiState(
    val isSyncEnabled: Boolean = false,
    val syncProvider: SyncProvider = SyncProvider.NONE,
    val lastSyncTime: String? = null,
    val isSyncing: Boolean = false,
    val syncResult: String? = null,
    val autoSync: Boolean = false,
    val wifiOnly: Boolean = true
)

enum class SyncProvider(val label: String) { NONE("None"), WEBDAV("WebDAV"), DROPBOX("Dropbox"), GOOGLE_DRIVE("Google Drive") }

@HiltViewModel
class CloudSyncSettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CloudSyncSettingsUiState())
    val uiState: StateFlow<CloudSyncSettingsUiState> = _uiState.asStateFlow()

    fun setSyncEnabled(enabled: Boolean) { _uiState.update { it.copy(isSyncEnabled = enabled) } }
    fun setProvider(provider: SyncProvider) { _uiState.update { it.copy(syncProvider = provider) } }
    fun setAutoSync(enabled: Boolean) { _uiState.update { it.copy(autoSync = enabled) } }
    fun setWifiOnly(wifiOnly: Boolean) { _uiState.update { it.copy(wifiOnly = wifiOnly) } }

    fun syncNow() {
        _uiState.update { it.copy(isSyncing = true) }
        _uiState.update { it.copy(isSyncing = false, lastSyncTime = "Just now", syncResult = "Sync completed successfully") }
    }
}
