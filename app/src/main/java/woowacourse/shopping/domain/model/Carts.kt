package woowacourse.shopping.domain.model

import woowacourse.shopping.feature.cart.adapter.CartGoodsItem
import java.io.Serializable

data class Carts(
    val carts: List<CartGoodsItem>,
    val totalQuantity: Int,
) : Serializable {
    val buyCarts: List<Cart> =
        carts.filter { it.isChecked }.map { it.cart }

    fun findTargetProductForBogo(standardQuantity: Int): Int =
        buyCarts
            .filter { it.quantity >= standardQuantity }
            .maxByOrNull { it.product.price }
            ?.product
            ?.price
            ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다")
}
