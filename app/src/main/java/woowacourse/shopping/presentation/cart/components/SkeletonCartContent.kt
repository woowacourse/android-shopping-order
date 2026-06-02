package woowacourse.shopping.presentation.cart.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.theme.Gray20
import woowacourse.shopping.ui.theme.Gray30

@Composable
fun SkeletonCartContent(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        repeat(3) {
            SkeletonCartCard(modifier = Modifier.height(200.dp).padding(10.dp))
        }
    }
}

@Composable
fun SkeletonCartCard(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .background(Gray20)
                .fillMaxSize()
                .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
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
                    .height(154.dp)
                    .padding(end = 80.dp)
                    .background(Gray30),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SkeletonCartCardPreview() {
    AndroidshoppingTheme {
        SkeletonCartCard()
    }
}
