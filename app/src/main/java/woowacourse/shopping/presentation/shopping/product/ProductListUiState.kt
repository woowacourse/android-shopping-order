package woowacourse.shopping.presentation.shopping.product

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.presentation.shopping.detail.ProductUi

data class ProductListUiState(
    val cart: Cart = Cart(),
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val products: List<ShoppingUiModel.Product> = emptyList(),
    val loadMoreModel: ShoppingUiModel.LoadMore? = null,
    val recentProducts: List<ProductUi> = emptyList(),
) {
    val totalProducts: List<ShoppingUiModel>
        get() {
            if (loadMoreModel == null) return products
            return products + loadMoreModel
        }
}
