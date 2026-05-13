package woowacourse.shopping.repository.network

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.network.dto.ProductResponse
import woowacourse.shopping.network.service.ProductService
import woowacourse.shopping.repository.ProductRepository

class RetrofitProductRepository(
    private val service: ProductService
) : ProductRepository {
    override suspend fun getSize(): Int {
        return fetchProducts().size
    }

    override suspend fun getProducts(
        fromIndex: Int,
        count: Int
    ): List<Product> {
        val allProducts = fetchProducts()
        val safeFromIndex = fromIndex.coerceIn(0, allProducts.size)
        return allProducts.drop(safeFromIndex).take(count)
    }

    override suspend fun hasNext(currentIndex: Int): Boolean {
        val total = getSize()
        return currentIndex < total - 1
    }

    override suspend fun findProduct(id: Long): Product? {
        lateinit var response: ProductResponse
        try {
            response = service.getProduct(id = id)
            return Product(
                id = response.id,
                name = response.name,
                price = Money(response.price),
                imageUrl = response.imageUrl,
                category = response.category,
            )
        } catch (_: Exception) {
            return null
        }
    }

    private suspend fun fetchProducts(): List<Product> {
        val response = service.getProducts()
        return response.content.map {
            Product(
                id = it.id,
                name = it.name,
                price = Money(it.price),
                imageUrl = it.imageUrl,
                category = it.category
            )
        }
    }
}