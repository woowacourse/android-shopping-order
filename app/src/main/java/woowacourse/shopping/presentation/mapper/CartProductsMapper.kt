package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.CartProductsModel
import woowacouse.shopping.model.cart.CartProducts

fun CartProducts.toUIModel(): CartProductsModel = CartProductsModel(getAll().map { it.toUIModel() })

fun CartProductsModel.toModel(): CartProducts = CartProducts(carts.map { it.toModel() })
