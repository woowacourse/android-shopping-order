package woowacourse.shopping.presentation.productlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.visible
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.theme.Gray30

@Composable
fun SkeletonProductsContent(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        repeat(3) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(250.dp),
            ) {
                SkeletonProductCard(modifier = Modifier.weight(1f))
                SkeletonProductCard(modifier = Modifier.weight(1f).visible(it != 2))
            }
        }
    }
}

@Composable
fun SkeletonProductCard(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(154.dp)
                    .background(Gray30),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .padding(end = 14.dp)
                    .background(Gray30),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .padding(end = 80.dp)
                    .background(Gray30),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SkeletonProductCardPreview() {
    AndroidshoppingTheme {
        SkeletonProductCard()
    }
}
