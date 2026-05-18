package woowacourse.shopping.ui.productList

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.ui.util.LoadState

data class ProductListUiState(
    val products: List<ProductUiModel> = emptyList(),
    val recentProducts: List<Product> = emptyList(),
    val hasNextPage: Boolean = true,
    val loadState: LoadState = LoadState.Initial,
    val totalCartAmount: String = "0",
    val showCartAmountBadge: Boolean = false,
)
