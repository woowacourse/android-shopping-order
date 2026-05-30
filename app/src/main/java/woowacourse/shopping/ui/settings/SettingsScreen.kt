package woowacourse.shopping.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.common.component.Header

@Composable
fun SettingsScreen(
    isPaymentReminderEnabled: Boolean,
    onTogglePaymentReminder: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            Header(
                text = "설정",
                onBackClick = onBackClick,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "미결제 알림",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "결제 화면 진입 후 5분 동안 결제하지 않으면 알림을 받습니다.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Switch(
                    checked = isPaymentReminderEnabled,
                    onCheckedChange = onTogglePaymentReminder,
                    colors =
                        SwitchDefaults.colors(
                            checkedTrackColor = Color.Black,
                        ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        isPaymentReminderEnabled = true,
        onTogglePaymentReminder = {},
        onBackClick = {},
    )
}
