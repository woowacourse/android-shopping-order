package woowacourse.shopping.domain

class Cart(
    val cartContents: List<CartContent>,
) {
    fun plusCartContent(newCartContent: CartContent): Cart {
        val duplicateCartItem =
            cartContents.firstOrNull { cartContent ->
                cartContent.hasProductId(newCartContent.productId)
            }

        if (duplicateCartItem != null) {
            return Cart(
                cartContents.map {
                    if (it.hasProductId(newCartContent.productId)) {
                        it.addQuantity(newCartContent.quantity)
                    } else {
                        it
                    }
                },
            )
        }
        return Cart(cartContents + newCartContent)
    }

    fun minusCartContent(target: CartContent): Cart {
        val duplicateCartItem =
            cartContents.firstOrNull { cartContent ->
                cartContent.hasProductId(target.productId)
            }
        require(duplicateCartItem != null) { "존재하지 않는 상품입니다." }
        require(duplicateCartItem.quantity >= target.quantity) { "존재하는 수량보다 많이 뺄 수 없습니다." }

        if (duplicateCartItem.quantity == target.quantity) {
            return Cart(cartContents.filter { !it.hasProductId(target.productId) })
        }
        return Cart(
            cartContents.map {
                if (it.hasProductId(target.productId)) {
                    it.decreaseQuantity(target.quantity)
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
}
