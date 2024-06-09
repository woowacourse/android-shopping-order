package woowacourse.shopping.view.detail

import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.model.RecentProduct

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: ProductItemDomain? = null,
    val lastlyViewedProduct: RecentProduct? = null,
    val cartItems: List<CartData> = emptyList(),
    val quantity: Int = 1,
)
