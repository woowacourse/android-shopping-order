package woowacourse.shopping.ui.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.common.theme.Gray5

@Composable
fun SettingsItem(
    headerText: String,
    bodyText: String,
    bodyInfoText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Box(
            modifier =
                Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFF3F3F3))
                    .padding(12.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            Text(
                text = headerText,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                color = Gray5,
            )
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 15.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = bodyText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = Color.Black,
                )
                Text(
                    text = bodyInfoText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500,
                    color = Gray5,
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors =
                    SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Black,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsItemPreview() {
    var checked by remember { mutableStateOf(true) }
    SettingsItem(
        headerText = "알림",
        bodyText = "미결제 알림",
        bodyInfoText = "5분 뒤 알림 받기",
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier,
    )
}
