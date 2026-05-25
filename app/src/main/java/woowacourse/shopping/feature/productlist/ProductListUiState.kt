package woowacourse.shopping.feature.productlist

import woowacourse.shopping.feature.common.state.ProductUiModel

data class ProductListUiState(
    val productUiModels: List<ProductUiModel> = emptyList(),
    val recentProducts: List<ProductUiModel> = emptyList(),
    val mostRecentProductId: Long? = null,
    val isLoading: Boolean = true,
    val isEnd: Boolean = false,
    val cartTotalQuantity: Int = 0,
)
