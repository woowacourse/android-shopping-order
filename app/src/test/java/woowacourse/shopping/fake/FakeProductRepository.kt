package woowacourse.shopping.fake

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.math.min

class FakeProductRepository(savedProducts: List<Product> = emptyList()) : ProductRepository {
    private val products: MutableList<Product> = savedProducts.toMutableList()

    override suspend fun find(id: Int): Result<Product> {
        val product = products.find { it.id == id }
        if (product != null) {
            return Result.success(product)
        }
        return Result.failure(IllegalArgumentException())
    }

    override suspend fun findPage(
        page: Int,
        pageSize: Int,
    ): Result<List<Product>> {
        val fromIndex = page * pageSize
        val toIndex = min(fromIndex + pageSize, products.size)
        val products = products.subList(fromIndex, toIndex)
        return Result.success(products)
    }

    override suspend fun isLastPage(
        page: Int,
        pageSize: Int,
    ): Result<Boolean> {
        val maxPage = products.size / pageSize - 1
        val isLastPage = pageSize == maxPage
        return Result.success(isLastPage)
    }

    override suspend fun findRecommendProducts(
        category: String,
        cartItems: List<CartItem>,
    ): Result<List<Product>> {
        val categoryProducts = products.filter { it.category == category }

        val recommendProducts =
            categoryProducts
                .filter { product -> cartItems.none { it.product.id == product.id } }
                .take(RECOMMEND_PRODUCTS_COUNT)

        return Result.success(recommendProducts)
    }

    companion object {
        private const val RECOMMEND_PRODUCTS_COUNT = 10
    }
}
