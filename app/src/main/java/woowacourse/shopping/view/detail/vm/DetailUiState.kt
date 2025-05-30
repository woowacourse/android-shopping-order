package woowacourse.shopping.view.detail.vm

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.main.state.ProductState

data class DetailUiState(
    val product: ProductState,
    val lastSeenProduct: Product?,
) {
    val category: String
        get() = product.item.category

    val cartQuantity: Quantity
        get() = product.cartQuantity

    val productId: Long
        get() = product.productId

    val productPrice: Int
        get() = product.item.priceValue * product.cartQuantity.value

    val hasLastSeenProduct: Boolean
        get() = lastSeenProduct != null

    fun addQuantity(quantity: Quantity): Quantity {
        return quantity + cartQuantity.value
    }
}
