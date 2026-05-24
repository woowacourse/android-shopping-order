package woowacourse.shopping.feature.setting

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import woowacourse.shopping.feature.common.component.CommonAppBar

@Composable
fun SettingScreen(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val sharedPref =
        remember {
            context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        }
    var isNotificationEnabled by remember {
        mutableStateOf(sharedPref.getBoolean("notification", true))
    }

    fun showToastMessage(checked: Boolean) {
        if (checked) {
            Toast.makeText(context, "알림 권한을 활성화하였습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "알림 권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color.White,
        modifier =
            modifier
                .fillMaxSize(),
        topBar = {
            CommonAppBar(
                title = "Setting",
                onCloseClick = onCloseClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("결제 알림 설정", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Switch(
                    checked = isNotificationEnabled,
                    onCheckedChange = {
                        isNotificationEnabled = it
                        sharedPref.edit { putBoolean("notification", it) }
                        showToastMessage(it)
                    },
                )
            }
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    SettingScreen(
        onCloseClick = {},
    )
}
