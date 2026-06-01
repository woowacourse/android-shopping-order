package woowacourse.shopping.ui.settings

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.appContainer
import woowacourse.shopping.notification.permission.hasNotificationPermission
import woowacourse.shopping.ui.settings.component.SettingsItem
import woowacourse.shopping.ui.settings.component.SettingsTopBar

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = rememberSettingsViewModel(),
) {
    val context = LocalContext.current
    val isNotificationEnabled by viewModel.isNotificationEnabled.collectAsStateWithLifecycle()
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            viewModel.onPermissionResult(granted)
        }

    LaunchedEffect(Unit) {
        viewModel.requestPermission.collect {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.showPermissionDeniedMessage.collect {
            Toast.makeText(context, "설정에서 알림 권한을 허용해 주세요", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = modifier) {
        SettingsTopBar(
            modifier = Modifier,
            onBackClick = onBackClick,
        )

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize(),
        ) {
            SettingsItem(
                headerText = "알림",
                bodyText = "미결제 알림",
                bodyInfoText = "5분 뒤 알림 받기",
                checked = isNotificationEnabled,
                onCheckedChange = { viewModel.updateSetting(it) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun rememberSettingsViewModel(): SettingsViewModel {
    val context = LocalContext.current
    return viewModel(
        factory =
            SettingsViewModel.provideFactory(
                container = appContainer(),
                hasNotificationPermission = { context.hasNotificationPermission() },
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        onBackClick = {},
        modifier = Modifier,
    )
}
