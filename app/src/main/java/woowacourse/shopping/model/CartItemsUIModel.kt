package woowacourse.shopping.model

import java.io.Serializable

data class CartItemsUIModel(
    val cartProducts: List<CartProductUIModel>,
    val totalPrice: Int,
    val itemCount: Int,
) : Serializable
