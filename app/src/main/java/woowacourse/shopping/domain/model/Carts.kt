package woowacourse.shopping.domain.model

import woowacourse.shopping.feature.cart.adapter.CartGoodsItem
import java.io.Serializable

data class Carts(
    val carts: List<CartGoodsItem>,
    val totalQuantity: Int,
) : Serializable
