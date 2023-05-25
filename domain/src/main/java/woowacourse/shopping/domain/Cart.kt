package woowacourse.shopping.domain

data class Cart(val cartProducts: List<CartProduct>) {
    fun add(cartProduct: CartProduct): Cart {
        return Cart(cartProducts + cartProduct)
    }

    fun remove(cartProduct: CartProduct): Cart {
        return Cart(cartProducts - cartProduct)
    }

    fun getSubCart(from: Int, to: Int): Cart {
        val to = if (to <= cartProducts.size) to else cartProducts.size
        return Cart(cartProducts.subList(from, to))
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

    fun findCartProduct(cartProduct: CartProduct): CartProduct? = cartProducts.find { it.id == cartProduct.id }
}
