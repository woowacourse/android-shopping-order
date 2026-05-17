package woowacourse.shopping.ui.productList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.common.shimmerEffect

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoadingCard(
                modifier = Modifier.weight(1f),
            )
            LoadingCard(
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoadingCard(
                modifier = Modifier.weight(1f),
            )
            LoadingCard(
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoadingCard(
                modifier = Modifier.weight(1f),
            )
            LoadingCard(
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoadingCard(
                modifier = Modifier.weight(1f),
            )
            LoadingCard(
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun LoadingCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(154.dp)
                    .shimmerEffect(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
                    .shimmerEffect(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .shimmerEffect(),
        )
    }
}

@Composable
@Preview
private fun LoadingContentPreview() {
    LoadingContent(modifier = Modifier.fillMaxSize())
}

@Composable
@Preview
private fun LoadingCardPreview() {
    LoadingCard()
}
