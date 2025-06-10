package woowacourse.shopping.domain.product

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

data class RecommendProducts(
    private val products: List<Product>,
    private val shoppingCartItem: List<ShoppingCartProduct>,
) {
    init {
        require(products.isNotEmpty()) { ERR_MUST_HAVE_ONE_PRODUCT }
    }

    fun get(): List<Product> {
        val category = products.first().category
        val cartProductIds: Set<Long> = shoppingCartItem.map { it.product.id }.toSet()
        return products
            .filter { !cartProductIds.contains(it.id) }
            .filter { it.category == category }
            .take(10)
    }

    companion object {
        private const val ERR_MUST_HAVE_ONE_PRODUCT = "1개의 상품이 있어야 합니다"
    }
}
