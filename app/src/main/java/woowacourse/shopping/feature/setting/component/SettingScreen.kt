package woowacourse.shopping.feature.setting.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.R
import woowacourse.shopping.feature.setting.SettingUiState
import woowacourse.shopping.feature.setting.SettingViewModel

@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = viewModel(factory = SettingViewModel.Factory),
) {
    LaunchedEffect(Unit) {
        viewModel.initialLoading()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onPaymentNotificationToggle = viewModel::toggleSetting,
        modifier = modifier,
    )
}

@Composable
fun SettingScreenContent(
    uiState: SettingUiState,
    onBackClick: () -> Unit,
    onPaymentNotificationToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = Color.White,
        modifier = modifier.fillMaxSize(),
        topBar = {
            SettingAppBar(onBackClick = onBackClick)
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SettingToggleRow(
                title = stringResource(R.string.setting_payment_notification),
                description = stringResource(R.string.setting_payment_notification_description),
                checked = uiState.isPaymentNotificationEnabled,
                enabled = !uiState.isLoading,
                onCheckedChange = onPaymentNotificationToggle,
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xffebebeb),
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                color = Color(0xff222222),
            )
            Text(
                text = description,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = Color(0xff555555),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xff04c09e),
            ),
        )
    }
}

@Preview
@Composable
private fun SettingScreenContentPreview() {
    SettingScreenContent(
        uiState = SettingUiState(isPaymentNotificationEnabled = true),
        onBackClick = {},
        onPaymentNotificationToggle = {},
    )
}
