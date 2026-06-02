package woowacourse.shopping.ui.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.core.designsystem.theme.PrimaryGreen
import woowacourse.shopping.ui.settings.SettingUiState

@Composable
fun SettingBody(
    uiState: SettingUiState,
    onPaymentNotificationCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 26.dp)
                .padding(top = 36.dp),
    ) {
        PaymentNotificationSettingItem(
            checked = uiState.isPaymentNotificationEnabled,
            onCheckedChange = onPaymentNotificationCheckedChange,
            modifier = Modifier.padding(top = 28.dp),
        )
    }
}

@Composable
private fun PaymentNotificationSettingItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .border(width = 1.dp, Color.Gray)
                .padding(horizontal = 20.dp, vertical = 18.dp),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = "미결제 알림",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
            )
            Text(
                text = "결제 화면 진입 후 5분 동안 결제가 완료되지 않으면 알려드려요.",
                fontSize = 15.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.PrimaryGreen,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray,
                    uncheckedBorderColor = Color.Gray,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingBodyPreview() {
    SettingBody(
        uiState = SettingUiState(isPaymentNotificationEnabled = true),
        onPaymentNotificationCheckedChange = {},
    )
}
