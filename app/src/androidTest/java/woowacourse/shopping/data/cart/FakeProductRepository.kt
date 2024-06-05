package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.ProductRepository

class FakeProductRepository(
    private val products: List<Product>,
) : ProductRepository {
    override fun loadProducts(
        currentPage: Int,
        size: Int,
    ): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun loadProducts(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<List<Product>> {
        return Result.success(products)
    }

    override fun findProductById(id: Long): Result<Product> {
        val product =
            products.find { it.id == id } ?: return Result.failure(
                NoSuchElementException("Invalid product id"),
            )
        return Result.success(product)
    }

    override fun canLoadMore(
        page: Int,
        size: Int,
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun loadRecentProducts(size: Int): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun saveRecentProduct(id: Long): Result<Long> {
        TODO("Not yet implemented")
    }
}
