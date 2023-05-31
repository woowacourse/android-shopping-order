package woowacourse.shopping.domain

data class Cart(val cartProducts: List<CartProduct>) {
    val selectedCart: Cart
        get() = Cart(cartProducts.filter { it.isChecked })
    val totalPrice: Int = cartProducts.sumOf { it.product.price * it.quantity }
    val totalQuantity: Int = cartProducts.sumOf { it.quantity }

    fun add(cartProduct: CartProduct): Cart {
        return Cart(cartProducts + cartProduct)
    }

    fun remove(cartProduct: CartProduct): Cart {
        return Cart(cartProducts - cartProduct)
    }

    fun replaceCartProduct(cartProduct: CartProduct): Cart {
        return Cart(
            cartProducts.map {
                if (it.id == cartProduct.id) cartProduct
                else it
            }
        )
    }

    fun removeCartProduct(cartProduct: CartProduct): Cart {
        return Cart(cartProducts.filter { it != cartProduct })
    }
}
