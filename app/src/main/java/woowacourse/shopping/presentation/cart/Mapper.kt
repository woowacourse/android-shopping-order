package woowacourse.shopping.presentation.cart

import com.example.domain.model.CartItem

fun CartItem.toCartUiModel(): CartUiModel = CartUiModel(this.id, product, quantity, false)

fun List<CartItem>.toCartUiModels(): List<CartUiModel> = map { it.toCartUiModel() }

fun CartUiModel.toCartItem(): CartItem = CartItem(this.cartItemId, product, quantity)

fun List<CartUiModel>.toCartItems(): List<CartItem> = map { it.toCartItem() }
