package woowacourse.shopping.ui.common.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadCartItem(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .width(324.dp)
                .height(152.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .padding(end = 90.dp)
                        .skeleton(),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(end = 175.dp)
                        .skeleton(),
            )
        }
    }
}

@Composable
fun LoadCartItemColumn(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        LoadCartItem()
        LoadCartItem()
        LoadCartItem()
        LoadCartItem()
        LoadCartItem()
    }
}

@Preview
@Composable
fun LoadingCartItemPreview() {
    LoadCartItem()
}
