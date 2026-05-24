package woowacourse.shopping.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.theme.Green40

@Composable
fun SettingScreen(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
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
                        text = "Setting",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                },
            )
        },
        modifier = Modifier.systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "미결제 알림",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium,
                )

                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(checkedTrackColor = Green40),
                )
            }
        }
    }
}
