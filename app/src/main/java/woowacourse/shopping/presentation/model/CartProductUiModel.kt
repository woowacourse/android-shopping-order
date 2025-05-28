package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.CartProduct

data class CartProductUiModel(
    val cartId: Long,
    val productId: Long,
    val productName: String,
    val imageUrl: String,
    val quantity: Int,
    val totalPrice: Int,
)

fun CartProduct.toCartItemUiModel() =
    CartProductUiModel(
        cartId,
        product.id,
        product.name,
        product.imageUrl,
        quantity,
        totalPrice,
    )
