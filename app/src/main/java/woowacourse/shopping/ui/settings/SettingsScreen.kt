package woowacourse.shopping.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import woowacourse.shopping.ui.settings.component.SettingsItem
import woowacourse.shopping.ui.settings.component.SettingsTopBar

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsTopBar(
            modifier = Modifier,
            onBackClick = onBackClick
        )

        Box(
            modifier = Modifier.weight(1f).fillMaxSize()
        ) {
            SettingsItem(
                headerText = "알림",
                bodyText = "미결제 알림",
                bodyInfoText = "5분 뒤 알림 받기",
                checked = true, // 이후 ViewModel 구현
                onCheckedChange = {}, // 이후 ViewModel 구현
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        onBackClick = {},
        modifier = Modifier
    )
}
