package woowacourse.shopping.recommender

import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Product

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
            return getFilteredProducts(allProductItems, allCartItem)
        }
        val filteredByCategory = getFilteredProducts(filteredItems, allCartItem)
        if (filteredByCategory.isEmpty()) {
            return getFilteredProducts(allProductItems, allCartItem)
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
    ): List<Product> {
        val productsInCart = allCartItem.map { it.product }
        return (products - productsInCart.toSet()).take(MAX_PRODUCT_SIZE)
    }
}
