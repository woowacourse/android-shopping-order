package woowacourse.shopping.feature.recommend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.constants.MockData
import woowacourse.shopping.feature.common.state.ProductUiModel
import woowacourse.shopping.feature.productlist.component.ProductItem

@Composable
fun RecommendList(
    isLoading: Boolean,
    products: List<ProductUiModel>,
    onIncrease: (Long) -> Unit,
    onDecrease: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier =
            modifier
                .fillMaxWidth(),
    ) {
        items(
            key = { it.id },
            items = products,
        ) {
            ProductItem(
                isLoading = isLoading,
                imageUrl = it.imageUrl,
                name = it.name,
                price = it.formattedPrice(),
                quantity = it.quantity,
                onIncrease = { onIncrease(it.id) },
                onDecrease = { onDecrease(it.id) },
                modifier = Modifier.width(100.dp),
            )
        }
    }
}

@Preview
@Composable
private fun RecommendListPreview() {
    RecommendList(
        isLoading = false,
        products =
            MockData.MOCK_PRODUCTS.map {
                ProductUiModel(
                    name = it.name,
                    price = 1000,
                    imageUrl = it.imageUrl,
                    id = it.id,
                    quantity = 0,
                )
            },
        onIncrease = { },
        onDecrease = { },
    )
}
