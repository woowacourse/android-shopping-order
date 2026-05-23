package woowacourse.shopping.mockup

import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.product.ProductResponseResult
import woowacourse.shopping.model.Product

class MockProductRepository(
    private val products: List<Product>,
) : ProductRepository {
    var getProductsCallCount = 0
        private set

    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): ProductResponseResult {
        getProductsCallCount++
        val filteredProducts =
            if (category.isBlank()) {
                products
            } else {
                products.filter { it.category == category }
            }
        val fromIndex = page * size
        val pageProducts = filteredProducts.drop(fromIndex).take(size)

        return ProductResponseResult(
            products = pageProducts,
            isLastPage = fromIndex + pageProducts.size >= filteredProducts.size,
        )
    }

    override suspend fun getProductById(id: String): Product =
        products.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Product not found")
}
