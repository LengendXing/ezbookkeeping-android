package com.ezbookkeeping.android.ui.screen.account

import androidx.compose.runtime.Stable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.AccountEntity
import com.ezbookkeeping.android.data.repository.AccountRepository
import com.ezbookkeeping.android.data.repository.TransactionRepository
import com.ezbookkeeping.android.ui.component.PasswordInputSheet
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class MoveAllTransactionsUiState(
    val sourceAccount: AccountEntity? = null,
    val targetAccountId: Int? = null,
    val accounts: List<AccountEntity> = emptyList(),
    val transactionCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showPasswordConfirm: Boolean = false,
    val success: Boolean = false
)

@HiltViewModel
class MoveAllTransactionsViewModel @Inject constructor(
    private val accountRepo: AccountRepository,
    private val transactionRepo: TransactionRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(MoveAllTransactionsUiState())
    val uiState: StateFlow<MoveAllTransactionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepo.getAccounts(authState.userId)
                .distinctUntilChanged().collect { list -> _uiState.update { it.copy(accounts = list) } }
        }
    }

    fun setSourceAccount(account: AccountEntity) {
        _uiState.update { it.copy(sourceAccount = account, transactionCount = -1) }
        viewModelScope.launch {
            transactionRepo.getByDateRange(authState.userId, "", "")
                .first().count { it.sourceAccountId == account.id || it.destinationAccountId == account.id }
                .let { count -> _uiState.update { it.copy(transactionCount = count) } }
        }
    }

    fun setTargetAccountId(id: Int) { _uiState.update { it.copy(targetAccountId = id) } }

    fun requestMove() {
        val s = _uiState.value
        if (s.sourceAccount == null) { _uiState.update { it.copy(error = "Select source account") }; return }
        if (s.targetAccountId == null) { _uiState.update { it.copy(error = "Select target account") }; return }
        if (s.sourceAccount!!.id == s.targetAccountId) { _uiState.update { it.copy(error = "Source and target cannot be the same") }; return }
        _uiState.update { it.copy(showPasswordConfirm = true) }
    }

    fun confirmMove(password: String) {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showPasswordConfirm = false) }
            try {
                val sourceId = s.sourceAccount!!.id
                val targetId = s.targetAccountId!!
                val transactions = transactionRepo.getByDateRange(authState.userId, "", "").first()
                val toMove = transactions.filter { it.sourceAccountId == sourceId || it.destinationAccountId == sourceId }
                toMove.forEach { tx ->
                    val updated = when {
                        tx.sourceAccountId == sourceId -> tx.copy(sourceAccountId = targetId)
                        tx.destinationAccountId == sourceId -> tx.copy(destinationAccountId = targetId)
                        else -> tx
                    }
                    transactionRepo.upsert(updated)
                }
                _uiState.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun dismissPasswordConfirm() { _uiState.update { it.copy(showPasswordConfirm = false) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveAllTransactionsScreen(navController: NavController, sourceAccountId: Int? = null) {
    val vm: MoveAllTransactionsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(state.success) { if (state.success) navController.popBackStack() }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Move All Transactions") },
            navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } }
        )
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Move all transactions from one account to another. This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Text("Source Account", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(state.accounts) { account ->
                    val selected = state.sourceAccount?.id == account.id
                    ListItem(
                        headlineContent = { Text(account.name) },
                        supportingContent = { Text("${account.currency} · ${account.balance}") },
                        trailingContent = { if (selected) Text("✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.clickable { vm.setSourceAccount(account) }
                    )
                }
            }

            if (state.sourceAccount != null && state.transactionCount >= 0) {
                Text("${state.transactionCount} transactions will be moved", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            HorizontalDivider()

            Text("Target Account", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(state.accounts.filter { it.id != state.sourceAccount?.id }) { account ->
                    val selected = state.targetAccountId == account.id
                    ListItem(
                        headlineContent = { Text(account.name) },
                        supportingContent = { Text("${account.currency} · ${account.balance}") },
                        trailingContent = { if (selected) Text("✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.clickable { vm.setTargetAccountId(account.id) }
                    )
                }
            }

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { vm.requestMove() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onError)
                else Text("Move All Transactions")
            }
        }
    }

    PasswordInputSheet(
        visible = state.showPasswordConfirm,
        title = "Confirm Move",
        message = "Enter your password to confirm moving ${state.transactionCount} transactions",
        onDismiss = { vm.dismissPasswordConfirm() },
        onConfirm = { vm.confirmMove(it) }
    )
}
