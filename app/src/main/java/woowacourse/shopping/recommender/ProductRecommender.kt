package woowacourse.shopping.recommender

import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Product

object ProductRecommender {
    fun recommendProduct(
        lastViewedItem: Product?,
        allProductItems: List<Product>,
        allCartItem: List<CartItem>,
        maxProductSize: Int,
    ): List<Product> {
        if (lastViewedItem == null) {
            return getFilteredProducts(allProductItems, allCartItem, maxProductSize)
        }
        val filteredItems = filterByCategory(lastViewedItem, allProductItems)
        if (filteredItems.isEmpty()) {
            return getFilteredProducts(allProductItems, allCartItem, maxProductSize)
        }
        val filteredByCategory = getFilteredProducts(filteredItems, allCartItem, maxProductSize)
        if (filteredByCategory.isEmpty()) {
            return getFilteredProducts(allProductItems, allCartItem, maxProductSize)
        }
        return filteredByCategory
    }

    private fun filterByCategory(
        lastViewedItem: Product,
        allProductItems: List<Product>,
    ) = allProductItems.filter { it.category == lastViewedItem.category }

    private fun getFilteredProducts(
        products: List<Product>,
        allCartItem: List<CartItem>,
        maxProductSize: Int,
    ): List<Product> {
        val productsInCart = allCartItem.map { it.product }
        return (products - productsInCart.toSet()).take(maxProductSize)
    }
}
