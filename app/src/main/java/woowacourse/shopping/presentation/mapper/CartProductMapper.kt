package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.CartModel
import woowacouse.shopping.model.cart.CartProduct

fun CartProduct.toUIModel(): CartModel = CartModel(id, product.toUIModel(), count, checked)

fun CartModel.toModel(): CartProduct = CartProduct(id, product.toModel(), count, checked)
