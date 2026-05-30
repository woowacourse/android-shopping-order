package woowacourse.shopping.presentation.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.presentation.setting.ui.SettingScreen
import woowacourse.shopping.presentation.setting.viewmodel.SettingViewModel

@Composable
fun SettingRoute(
    onBack: () -> Unit,
    viewModel: SettingViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingScreen(
        isNotificationEnabled = uiState.isPaymentPendingNotificationEnabled,
        onToggleNotification = viewModel::togglePaymentPendingNotification,
        onBack = onBack,
    )
}
