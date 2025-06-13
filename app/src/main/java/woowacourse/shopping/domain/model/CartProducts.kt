package woowacourse.shopping.domain.model

import java.io.Serializable

class CartProducts(
    val value: List<CartProduct>,
) : Serializable {
    val totalPrice: Int = value.sumOf { it.totalPrice }

    val totalQuantity: Int = value.sumOf { it.quantity }

    val ids: List<Int> = value.map { it.id }

    operator fun plus(cartProduct: CartProduct): CartProducts = CartProducts(value + cartProduct)

    operator fun plus(cartProducts: List<CartProduct>): CartProducts = CartProducts(value + cartProducts)

    operator fun minus(cartProduct: CartProduct): CartProducts = CartProducts(value - cartProduct)

    operator fun minus(cartProducts: List<CartProduct>): CartProducts = CartProducts(value - cartProducts.toSet())

    operator fun contains(cartProduct: CartProduct): Boolean = value.contains(cartProduct)
}
