package woowacourse.shopping.ui.shopping.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun ShoppingScreenSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ShoppingTopBar(
            cartCount = 0,
            modifier = Modifier,
            onCartClick = {},
            onSettingsClick = {}
        )

        ProductGroup(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
                    .shimmer(),
        )
    }
}

@Composable
private fun ProductGroup(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(4) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ProductItem()
                ProductItem()
            }
        }
    }
}

@Composable
private fun ProductItem(modifier: Modifier = Modifier) {
    val backColor = Color(0xFFE2E2E2)

    Column(
        modifier = modifier.size(width = 154.dp, height = 206.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = 154.dp, height = 158.62.dp)
                    .background(backColor),
        )
        Spacer(Modifier.size(9.27.dp))
        Box(
            modifier =
                Modifier
                    .size(width = 140.dp, height = 14.42.dp)
                    .background(backColor),
        )
        Spacer(Modifier.size(9.27.dp))
        Box(
            modifier =
                Modifier
                    .size(width = 118.dp, height = 14.42.dp)
                    .background(backColor),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShoppingScreenSkeletonPreview() {
    ShoppingScreenSkeleton()
}

@Preview(showBackground = true)
@Composable
private fun ProductGroupPreview() {
    ProductGroup()
}

@Preview(showBackground = true)
@Composable
private fun ProductItemPreview() {
    ProductItem()
}
