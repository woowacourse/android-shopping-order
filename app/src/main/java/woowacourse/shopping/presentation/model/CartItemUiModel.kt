package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.CartItem

data class CartItemUiModel(
    val id: Long = 0,
    val product: ProductUiModel,
    val quantity: Int = 0,
    val isSelected: Boolean = false,
    val totalPrice: Int,
)

fun CartItemUiModel.toDomain(): CartItem =
    CartItem(
        cartId = id,
        product = product.toDomain(),
        quantity = quantity,
    )

fun CartItem.toPresentation(): CartItemUiModel =
    CartItemUiModel(
        id = this.cartId,
        product = this.product.toPresentation(),
        quantity = this.quantity,
        totalPrice = this.totalPrice,
    )
