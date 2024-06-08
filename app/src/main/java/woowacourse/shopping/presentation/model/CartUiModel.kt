package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Cart
import java.io.Serializable

data class CartUiModel(
    val id: Int = EMPTY_CART_ID,
    val quantity: Int = INIT_QUANTITY_NUM,
    val product: ProductUiModel,
) : Serializable {
    val totalPrice: Int
        get() = product.price * quantity

    companion object {
        const val EMPTY_CART_ID = -1
        const val INIT_QUANTITY_NUM = 0
    }
}

fun CartUiModel.toDomain(): Cart {
    return Cart(
        id = this.id,
        quantity = this.quantity,
        product = this.product.toDomain(),
    )
}
