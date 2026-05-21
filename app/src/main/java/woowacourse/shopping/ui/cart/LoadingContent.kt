package woowacourse.shopping.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.common.shimmerEffect

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LoadingCartItem()
        LoadingCartItem()
    }
}

@Composable
private fun LoadingCartItem() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .shimmerEffect()
                .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .height(25.dp)
                    .fillMaxWidth(0.7f)
                    .background(Color(0xffE2E2E2)),
        )
        Box(
            modifier =
                Modifier
                    .height(70.dp)
                    .fillMaxWidth(0.5f)
                    .background(Color(0xffE2E2E2)),
        )
    }
}

@Preview
@Composable
fun LoadingContentPreview() {
    LoadingContent(modifier = Modifier.fillMaxSize())
}
