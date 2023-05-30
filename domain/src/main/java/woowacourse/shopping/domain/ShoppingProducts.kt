package woowacourse.shopping.domain

class ShoppingProducts(val value: List<ShoppingProduct>) {
    operator fun plus(products: ShoppingProducts): ShoppingProducts {
        return ShoppingProducts(value + products.value)
    }
}
