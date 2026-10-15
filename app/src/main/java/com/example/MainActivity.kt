package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.WalletBottomNavBar
import com.example.ui.screens.AddAccountDialog
import com.example.ui.screens.AddBudgetDialog
import com.example.ui.screens.AddPlannedPaymentDialog
import com.example.ui.screens.AddRecordSheet
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.NotificationsDialog
import com.example.ui.screens.PlanningScreen
import com.example.ui.screens.RecordsDialog
import com.example.ui.screens.SavingsGoalsDialog
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.ShareCostDialog
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.WalletViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                WalletMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WalletMainApp(viewModel: WalletViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val showAddRecord by viewModel.showAddRecordSheet.collectAsStateWithLifecycle()
    val showAddAccount by viewModel.showAddAccountDialog.collectAsStateWithLifecycle()
    val showAddBudget by viewModel.showAddBudgetDialog.collectAsStateWithLifecycle()
    val showAddPlannedPayment by viewModel.showAddPlannedPaymentDialog.collectAsStateWithLifecycle()
    val showNotifications by viewModel.showNotificationsDialog.collectAsStateWithLifecycle()
    val showSettings by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showRecords by viewModel.showRecordsDialog.collectAsStateWithLifecycle()
    val showSavingsGoals by viewModel.showSavingsGoalsDialog.collectAsStateWithLifecycle()
    val showShareCost by viewModel.showShareCostDialog.collectAsStateWithLifecycle()

    // Handle back button: if not on Dashboard, return to Dashboard
    BackHandler(enabled = currentTab != AppTab.DASHBOARD) {
        viewModel.setTab(AppTab.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        containerColor = DarkBackground,
        bottomBar = {
            WalletBottomNavBar(
                selectedTab = currentTab,
                onTabSelected = { tab -> viewModel.setTab(tab) },
                onAddClick = { viewModel.showAddRecordSheet.value = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentTab) {
                AppTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppTab.PLANNING -> PlanningScreen(viewModel = viewModel)
                AppTab.STATISTICS -> StatisticsScreen(viewModel = viewModel)
                AppTab.MORE -> MoreScreen(viewModel = viewModel)
            }
        }
    }

    // Modal Bottom Sheets and Dialogs (All 100% Free & Fully Functional)
    if (showAddRecord) {
        AddRecordSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.showAddRecordSheet.value = false }
        )
    }

    if (showAddAccount) {
        AddAccountDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showAddAccountDialog.value = false }
        )
    }

    if (showAddBudget) {
        AddBudgetDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showAddBudgetDialog.value = false }
        )
    }

    if (showAddPlannedPayment) {
        AddPlannedPaymentDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showAddPlannedPaymentDialog.value = false }
        )
    }

    if (showRecords) {
        RecordsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showRecordsDialog.value = false }
        )
    }

    if (showSavingsGoals) {
        SavingsGoalsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showSavingsGoalsDialog.value = false }
        )
    }

    if (showShareCost) {
        ShareCostDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showShareCostDialog.value = false }
        )
    }

    if (showNotifications) {
        NotificationsDialog(
            onDismiss = { viewModel.showNotificationsDialog.value = false }
        )
    }

    if (showSettings) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showSettingsDialog.value = false }
        )
    }
}
