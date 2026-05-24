package woowacourse.shopping.ui.setting

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun SettingRoute(
    navController: NavController,
    viewModel: SettingViewModel = viewModel(factory = SettingViewModel.Factory),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                SettingUiEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                is SettingUiEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    SettingScreen(
        checked = uiState.value.isNotification,
        onBackClick = viewModel::navigateBack,
        onCheckedChange = viewModel::updateIsNotification,
    )
}
