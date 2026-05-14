package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.data.model.Money
import woowacourse.shopping.data.model.Product

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val selectedQuantity: Int = 1,
    val lastViewedProduct: Product? = null,
) {
    val totalPrice: Money
        get() = (product?.price ?: Money(0)) * selectedQuantity
}
