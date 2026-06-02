package woowacourse.shopping.testing.fakes

import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.model.product.Product

class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()

    fun setProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
    }

    override suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): List<Product> = products

    override suspend fun getProduct(id: Long): Product = products.first { it.id == id }

    override suspend fun getCategoryProducts(
        page: Int,
        pageSize: Int,
        category: String
    ): List<Product> = products.filter { it.category == category }
}
