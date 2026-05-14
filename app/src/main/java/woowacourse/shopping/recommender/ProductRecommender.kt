package woowacourse.shopping.recommender

import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Product

object ProductRecommender {
    const val MAX_PRODUCT_SIZE = 10

    fun recommendProduct(
        lastViewedItem: Product?,
        allProductItems: List<Product>,
        allCartItem: List<CartItem>,
    ): List<Product> {
        if (lastViewedItem == null) {
            return getFilteredProducts(allProductItems, allCartItem)
        }
        val filteredItems = filterByCategory(lastViewedItem, allProductItems)

        if (filteredItems.isEmpty()) {
            // 싱위 상품 반환
            return getFilteredProducts(allProductItems, allCartItem)
        }
        // 같은 카테고리 상품 반환
        return getFilteredProducts(filteredItems, allCartItem)
    }

    private fun filterByCategory(
        lastViewedItem: Product,
        allProductItems: List<Product>
    ) = allProductItems.filter { it.category == lastViewedItem.category }

    private fun getFilteredProducts(
        products: List<Product>,
        allCartItem: List<CartItem>,
    ): List<Product> {
        val productsInCart = allCartItem.map { it.product }
        return (products - productsInCart.toSet()).take(MAX_PRODUCT_SIZE)
    }
}
