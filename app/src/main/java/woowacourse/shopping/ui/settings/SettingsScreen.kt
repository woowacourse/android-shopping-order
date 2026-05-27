package woowacourse.shopping.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.theme.Gray10
import woowacourse.shopping.ui.theme.Gray50

@Composable
fun SettingsScreenRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        isPaymentReminderEnabled = uiState.isPaymentReminderEnabled,
        onPaymentReminderEnabledChange = settingsViewModel::updatePaymentReminderEnabled,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(
    isPaymentReminderEnabled: Boolean,
    onPaymentReminderEnabledChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = Color.White,
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clickable { onBackClick() },
                    )
                    Spacer(modifier = Modifier.width(21.dp))
                    Text(
                        text = "설정",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                },
            )
        },
        modifier = modifier.statusBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            SettingSwitchRow(
                title = "미결제 알림",
                description = "결제가 되지 않았으면 5분뒤에 알림이 울려요",
                isChecked = isPaymentReminderEnabled,
                onCheckedChange = onPaymentReminderEnabledChange,
            )

            HorizontalDivider(
                thickness = 8.dp,
                color = Gray10,
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Text(
                text = description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Gray50,
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        isPaymentReminderEnabled = true,
        onPaymentReminderEnabledChange = {},
        onBackClick = {},
    )
}
