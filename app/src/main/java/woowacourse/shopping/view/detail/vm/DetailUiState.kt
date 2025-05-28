package woowacourse.shopping.view.detail.vm

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.main.state.ProductState

data class DetailUiState(
    val product: ProductState,
    val lastSeenProduct: Product?,
) {
    val cartQuantity: Quantity
        get() = product.cartQuantity

    val productId: Long
        get() = product.item.id

    val productPrice: Int
        get() = product.item.priceValue * product.cartQuantity.value

    val hasLastSeenProduct: Boolean
        get() = lastSeenProduct != null
}
