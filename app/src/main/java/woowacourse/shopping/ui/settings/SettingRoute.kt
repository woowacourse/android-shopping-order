package woowacourse.shopping.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import woowacourse.shopping.domain.repository.SettingRepository
import woowacourse.shopping.ui.event.UiEventHandler
import woowacourse.shopping.ui.navigation.ShoppingRoute

fun NavGraphBuilder.settingRoute(
    settingRepository: SettingRepository,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
) {
    composable<ShoppingRoute.Setting> {
        SettingRouteContent(
            settingRepository = settingRepository,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
        )
    }
}

@Composable
private fun SettingRouteContent(
    settingRepository: SettingRepository,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
) {
    val viewModel: SettingViewModel =
        viewModel(
            factory =
                SettingViewModelFactory(
                    settingRepository,
                ),
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                viewModel.setPaymentNotificationEnabled(true)
            } else {
                viewModel.setPaymentNotificationEnabled(false)
                viewModel.showNotificationPermissionDeniedMessage()
            }
        }

    UiEventHandler(
        uiEvent = viewModel.uiEvent,
        snackbarHostState = snackbarHostState,
    )

    SettingScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onPaymentNotificationCheckedChange = { enabled ->
            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionStatus =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS,
                    )
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    viewModel.setPaymentNotificationEnabled(true)
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                viewModel.setPaymentNotificationEnabled(enabled)
            }
        },
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
    )
}
