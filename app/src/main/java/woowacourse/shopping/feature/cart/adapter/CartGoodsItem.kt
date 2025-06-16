package woowacourse.shopping.feature.cart.adapter

import woowacourse.shopping.domain.model.Cart
import java.io.Serializable

data class CartGoodsItem(
    val cart: Cart,
    val isChecked: Boolean = false,
) : Serializable
