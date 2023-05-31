package woowacouse.shopping.model.cart

class CartProducts(
    private val carts: List<CartProduct>
) {
    val totalPrice: Int
        get() = carts.sumOf { if (it.checked) it.product.price * it.count else 0 }

    val totalCount: Int
        get() = carts.sumOf { it.count }

    fun addCart(cartProduct: CartProduct): CartProducts {
        val newCarts = carts.toMutableList()
        newCarts.add(cartProduct)
        return CartProducts(newCarts.toList())
    }

    fun deleteCart(cartId: Long): CartProducts {
        return CartProducts(carts.filterNot { it.id == cartId })
    }

    // TODO 테스트 코드 작성
    fun updateCartId(cartId: Long, productId: Long): CartProducts {
        return CartProducts(
            carts.map {
                if (it.product.id == productId) {
                    it.copy(id = cartId)
                } else {
                    it
                }
            }
        )
    }

    fun updateCartChecked(cartId: Long, checked: Boolean): CartProducts {
        return CartProducts(
            carts.map {
                if (it.id == cartId) {
                    it.copy(checked = checked)
                } else {
                    it
                }
            }
        )
    }

    fun updateCartCountByCartId(cartId: Long, count: Int): CartProducts {
        return CartProducts(
            carts.map {
                if (it.id == cartId) {
                    it.copy(count = count)
                } else {
                    it
                }
            }
        )
    }

    // TODO 테스트 코드 작성
    fun updateCartCountByCartIds(cartIds: Map<Long, Int>): CartProducts {
        return CartProducts(
            carts.map { cart ->
                val count = cartIds[cart.id] ?: 0
                cart.updateCount(count)
            }
        )
    }

    fun updateAllCartsChecked(cartIds: List<Long>, checked: Boolean): CartProducts {
        val newCartProducts = carts.map {
            if (it.id in cartIds) {
                it.copy(checked = checked)
            } else {
                it
            }
        }
        return CartProducts(newCartProducts)
    }

    fun getCartByCartId(cartId: Long): CartProduct? =
        carts.find { it.id == cartId }

    fun getCartByProductId(productId: Long): CartProduct? =
        carts.find { it.product.id == productId }

    fun getAll(): List<CartProduct> = carts.toList()

    // TODO 테스트 코드 작성
    fun getDisplayList(lastDisplayedIndex: Int, displayCountCondition: Int): List<CartProduct> {
        val subToIndex = if (carts.size > lastDisplayedIndex + displayCountCondition) {
            lastDisplayedIndex + displayCountCondition
        } else {
            carts.size
        }
        return carts.subList(0, subToIndex)
    }
}
