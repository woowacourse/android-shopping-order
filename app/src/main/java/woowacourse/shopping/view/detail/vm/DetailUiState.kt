package woowacourse.shopping.view.detail.vm

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.detail.DetailUiEvent
import woowacourse.shopping.view.main.state.ProductState

data class DetailUiState(
    val product: ProductState,
    val lastSeenProduct: Product? = null,
) {
    val category: String = product.item.category

    val cartQuantity: Quantity = product.cartQuantity

    val productId: Long = product.productId

    val productPrice: Int = product.item.priceValue * product.cartQuantity.value

    val hasLastSeenProduct: Boolean = lastSeenProduct != null

    fun addQuantity(quantity: Quantity): Quantity {
        return quantity + cartQuantity.value
    }

    fun increaseQuantity(): DetailUiState {
        val increased = product.increaseCartQuantity()
        return copy(product = increased)
    }

    fun decreaseQuantity(): Pair<DetailUiState, DetailUiEvent.ShowCannotDecrease?> {
        val decreased = product.decreaseCartQuantity()
        return if (decreased.hasCartQuantity) {
            copy(product = decreased) to null
        } else {
            copy(product = product.copy(cartQuantity = Quantity(1))) to DetailUiEvent.ShowCannotDecrease
        }
    }
}
