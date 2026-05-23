package woowacourse.shopping.presentation.setting.ui

import android.R.attr.label
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.presentation.common.components.ShoppingAppBar
import woowacourse.shopping.presentation.setting.ui.components.SettingRow
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

@Composable
fun SettingScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White,
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clickable { onBack() },
                    )
                    Spacer(modifier = Modifier.width(21.dp))
                    Text(
                        text = stringResource(R.string.setting),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            SettingRow(
                label = stringResource(R.string.notification),
                isChecked = false,
                onCheckedChange = { },
            )
        }
    }
}

@Preview
@Composable
private fun SettingScreenPreview() {
    AndroidshoppingTheme {
        SettingScreen(
            onBack = {},
        )
    }
}
