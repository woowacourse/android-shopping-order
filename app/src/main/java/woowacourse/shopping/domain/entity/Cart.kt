package woowacourse.shopping.domain.entity

data class Cart(
    private val productMap: Map<Product, Int> = emptyMap(),
) {
    constructor(products: List<Product>) : this(products.groupingBy { it }.eachCount())

    constructor(vararg products: Product) : this(products.toList())

    fun add(product: Product): Cart {
        val count = productMap.getOrDefault(product, 0) + 1
        return Cart(productMap.plus(product to count))
    }

    fun remove(product: Product): Cart {
        if (!productMap.containsKey(product)) return this
        if (productMap[product] == 1) {
            return Cart(productMap.minus(product))
        }
        val count = productMap[product] ?: return this
        return Cart(productMap.plus(product to count - 1))
    }

    fun totalPrice(): Int =
        productMap.map { (product, count) ->
            product.price * count
        }.sum()

    fun cartProducts(): List<CartProduct> =
        productMap.map { (product, count) ->
            CartProduct(product, count)
        }
}
