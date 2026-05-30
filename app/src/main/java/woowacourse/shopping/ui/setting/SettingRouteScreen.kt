package woowacourse.shopping.ui.setting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingRouteScreen(
    onBackClick: () -> Unit,
    viewModel: SettingViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        SettingScreen(
            uiState = uiState,
            modifier = Modifier.padding(innerPadding),
            onBackClick = onBackClick,
            onUnpaidNotificationEnabledChange = viewModel::setUnpaidNotificationEnabled,
        )
    }
}
