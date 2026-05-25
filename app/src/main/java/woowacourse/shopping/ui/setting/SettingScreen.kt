package woowacourse.shopping.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Green40

@Composable
fun SettingScreen(
    isNotificationEnabled: Boolean,
    onBackClick: () -> Unit,
    onToggleClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로 가기",
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
        modifier = modifier.systemBarsPadding(),
    ) { innerPadding ->
        SettingContent(
            isNotificationEnabled = isNotificationEnabled,
            onToggleClick = onToggleClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
private fun SettingContent(
    isNotificationEnabled: Boolean,
    onToggleClick: (Boolean) -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 18.dp, vertical = 12.dp),
    ) {
        SettingToggleRow(
            title = "앱 알림 설정",
            description = "앱 알림 수신 여부를 설정합니다.",
            toggleState = isNotificationEnabled,
            onToggleClick = onToggleClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    toggleState: Boolean,
    onToggleClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(42.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Text(
                text = description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray40,
            )
        }
        Switch(
            checked = toggleState,
            onCheckedChange = { onToggleClick(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Green40,
                checkedTrackColor = Color.White,
                uncheckedThumbColor = Gray40,
                uncheckedTrackColor = Color.White,
            ),
        )
    }
}

@Preview
@Composable
private fun SettingScreenPreview(){
    SettingScreen(
        isNotificationEnabled = true,
        onBackClick = {},
        onToggleClick = {},
    )
}
