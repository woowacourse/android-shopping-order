package woowacourse.shopping.fake

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.math.min

class FakeProductRepository(savedProducts: List<Product> = emptyList()) : ProductRepository {
    private val products: MutableList<Product> = savedProducts.toMutableList()

    override fun find(
        id: Int,
        callback: (Result<Product>) -> Unit,
    ) {
        val product = products.find { it.id == id }
        if (product != null) {
            callback(Result.success(product))
            return
        }
        callback(Result.failure(IllegalArgumentException()))
    }

    override fun syncFind(id: Int): Product? {
        return products.find { it.id == id }
    }

    override fun findPage(
        page: Int,
        pageSize: Int,
        callback: (Result<List<Product>>) -> Unit,
    ) {
        val fromIndex = page * pageSize
        val toIndex = min(fromIndex + pageSize, products.size)
        val products = products.subList(fromIndex, toIndex)
        callback(Result.success(products))
    }

    override fun isLastPage(
        page: Int,
        pageSize: Int,
        callback: (Result<Boolean>) -> Unit,
    ) {
        val maxPage = products.size / pageSize - 1
        val isLastPage = pageSize == maxPage
        callback(Result.success(isLastPage))
    }

    override fun findRecommendProducts(
        category: String,
        cartItems: List<CartItem>,
        callback: (Result<List<Product>>) -> Unit,
    ) {
        val categoryProducts = products.filter { it.category == category }

        val recommendProducts =
            categoryProducts
                .filter { product -> cartItems.none { it.productId == product.id } }
                .take(RECOMMEND_PRODUCTS_COUNT)

        callback(Result.success(recommendProducts))
    }

    companion object {
        private const val RECOMMEND_PRODUCTS_COUNT = 10
    }
}
