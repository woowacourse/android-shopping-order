package woowacourse.shopping.domain

class Cart(
    val cartContents: List<CartContent>,
) {
    private fun duplicateCartItem(newCartContent: CartContent): CartContent? =
        cartContents.firstOrNull { cartContent ->
            cartContent.hasProductId(newCartContent.productId)
        }

    fun plusCartContent(newCartContent: CartContent): Cart {
        val duplicateCartItem =
            cartContents.firstOrNull { cartContent ->
                cartContent.hasProductId(newCartContent.productId)
            }

        if (duplicateCartItem != null) {
            return Cart(
                cartContents.map {
                    if (it.hasProductId(newCartContent.productId)) {
                        it.addQuantity(newCartContent)
                    } else {
                        it
                    }
                },
            )
        }
        return Cart(cartContents + newCartContent)
    }

    fun minusCartContent(newCartContent: CartContent): Cart {
        require(this.hasCartContent(newCartContent)) { "존재하지 않는 상품입니다." }

        val duplicateCartItem = duplicateCartItem(newCartContent)!!

        if (duplicateCartItem.quantity == newCartContent.quantity) {
            return Cart(cartContents.filter { !it.hasProductId(newCartContent.productId) })
        }
        return Cart(
            cartContents.map {
                if (it.hasProductId(newCartContent.productId)) {
                    it.decreaseQuantity(newCartContent)
                } else {
                    it
                }
            },
        )
    }

    fun quantityOf(productId: Long): Int {
        val cartItem =
            cartContents.firstOrNull { cartContent ->
                cartContent.hasProductId(productId)
            }
        return cartItem?.quantity
            ?: 0
    }

    fun cartContentsSizeOf(): Int = cartContents.size

    fun totalQuantityOf(): Int =
        cartContents.sumOf {
            it.quantity
        }

    fun getProductList(): List<Product> = cartContents.map { it.product }

    fun hasCartContent(newCartContent: CartContent?): Boolean =
        cartContents.any { cartContent ->
            cartContent.hasProductId(newCartContent?.productId ?: 0)
        }
}
