package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.CartLocalEntity
import woowacouse.shopping.model.cart.CartProduct
import woowacouse.shopping.model.product.Product

private val EMPTY_PRODUCT = Product(-1, "", 0, "")

fun CartLocalEntity.toModel(): CartProduct = CartProduct(id, EMPTY_PRODUCT, 0, checked == 1)
