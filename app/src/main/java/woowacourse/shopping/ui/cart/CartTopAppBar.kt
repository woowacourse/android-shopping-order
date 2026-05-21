package woowacourse.shopping.ui.cart

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import woowacourse.shopping.constant.ShoppingColor.APP_BAR_COLOR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopAppBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "Cart",
                fontSize = 20.sp,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onClick,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color(APP_BAR_COLOR),
                scrolledContainerColor = Color.Unspecified,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
            ),
        windowInsets = WindowInsets(0, 0, 0, 0),
    )
}

@Preview
@Composable
fun CartTopAppBarPreview() {
    CartTopAppBar(
        onClick = {},
    )
}
