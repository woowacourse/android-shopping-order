package woowacourse.shopping.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import woowacourse.shopping.core.designsystem.component.layout.CommonFrame
import woowacourse.shopping.ui.settings.component.SettingBody
import woowacourse.shopping.ui.settings.component.SettingHeader

@Composable
fun SettingScreen(
    uiState: SettingUiState,
    onBackClick: () -> Unit,
    onPaymentNotificationCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    CommonFrame(
        headerContent = { SettingHeader(onBackClick = onBackClick) },
        bodyContent = {
            SettingBody(
                uiState = uiState,
                onPaymentNotificationCheckedChange = onPaymentNotificationCheckedChange,
            )
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SettingScreen(
        uiState = SettingUiState(isPaymentNotificationEnabled = true),
        onBackClick = {},
        onPaymentNotificationCheckedChange = {},
    )
}
