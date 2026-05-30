package woowacourse.shopping.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.local.SettingsPreferences

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val settingsPreferences = remember { SettingsPreferences(context) }
    val viewModel: SettingsViewModel =
        viewModel(
            factory = SettingsViewModel.factory(settingsPreferences),
        )
    val isPaymentReminderEnabled by viewModel.isPaymentReminderEnabled.collectAsStateWithLifecycle()

    SettingsScreen(
        isPaymentReminderEnabled = isPaymentReminderEnabled,
        onTogglePaymentReminder = viewModel::togglePaymentReminder,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}
