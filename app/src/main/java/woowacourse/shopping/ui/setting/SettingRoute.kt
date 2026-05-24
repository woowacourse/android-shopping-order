package woowacourse.shopping.ui.setting

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun SettingRoute(
    navController: NavController,
    viewModel: SettingViewModel = viewModel(factory = SettingViewModel.Factory),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    SettingScreen(
        checked = uiState.value.isNotification,
        onBackClick = { navController.popBackStack() },
        onCheckedChange = viewModel::updateIsNotification,
    )
}
