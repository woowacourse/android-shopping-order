package woowacourse.shopping.ui.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.theme.Gray20

@Composable
fun ProductCardSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Gray20),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 14.dp)
                .height(14.dp)
                .background(Gray20)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 36.dp)
                .height(14.dp)
                .background(Gray20)
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun ProductCardSkeletonPreview() {
    ProductCardSkeleton(
        modifier = Modifier
            .height(206.dp)
            .width(154.dp)
    )
}