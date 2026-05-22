package woowacourse.shopping.ui.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.R
import woowacourse.shopping.ui.ShoppingTypography
import woowacourse.shopping.ui.common.component.card.shoppingOutlinedCard
import woowacourse.shopping.ui.common.component.header.NavigationHeader
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun SettingScreen(
    uiState: SettingUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onUnpaidNotificationEnabledChange: (Boolean) -> Unit,
) {
    SettingScreenContent(
        title = stringResource(R.string.setting_title),
        unpaidNotificationTitle = stringResource(R.string.setting_unpaid_notification_title),
        unpaidNotificationDescription = stringResource(R.string.setting_unpaid_notification_description),
        uiState = uiState,
        modifier = modifier,
        onBackClick = onBackClick,
        onUnpaidNotificationEnabledChange = onUnpaidNotificationEnabledChange,
    )
}

@Composable
private fun SettingScreenContent(
    title: String,
    unpaidNotificationTitle: String,
    unpaidNotificationDescription: String,
    uiState: SettingUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onUnpaidNotificationEnabledChange: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        NavigationHeader(
            title = title,
            onBackClick = onBackClick,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .shoppingOutlinedCard()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = unpaidNotificationTitle,
                    style = ShoppingTypography.sectionTitle,
                    color = Color.Black,
                )
                Switch(
                    checked = uiState.isUnpaidNotificationEnabled,
                    onCheckedChange = onUnpaidNotificationEnabledChange,
                )
            }
            Text(
                text = unpaidNotificationDescription,
                style = ShoppingTypography.itemCaption,
                color = ShoppingColors.Gray4,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SettingScreenContent(
        title = "설정",
        unpaidNotificationTitle = "미결제 알림 기능",
        unpaidNotificationDescription = "결제 화면 진입 후 5분 동안 주문을 완료하지 않으면 알림을 받을 수 있어요.",
        uiState = SettingUiState(isUnpaidNotificationEnabled = true),
        onBackClick = {},
        onUnpaidNotificationEnabledChange = {},
    )
}
