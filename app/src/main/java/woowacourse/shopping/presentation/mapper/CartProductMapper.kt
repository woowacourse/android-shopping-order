package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.CartProductModel
import woowacouse.shopping.model.cart.CartProduct

fun CartProduct.toUIModel(): CartProductModel = CartProductModel(id, product.toUIModel(), count, checked)

fun CartProductModel.toModel(): CartProduct = CartProduct(id, product.toModel(), count, checked)
