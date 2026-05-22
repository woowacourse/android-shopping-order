package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.service.ProductService
import woowacourse.shopping.data.remote.server.dto.product.toDomain
import woowacourse.shopping.data.remote.server.dto.products.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productService: ProductService
): ProductRepository {
    override suspend fun getProducts(
        page: Int,
        pageSize: Int
    ): List<Product> {
        val response = productService.requestProducts(
            page = page,
            size = pageSize
        )
        return response.content.map { it.toDomain() }
    }

    override suspend fun getProduct(id: Long): Product {
        try {
            val response = productService.requestProduct(id)
            return response.toDomain()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getCategoryProducts(
        page: Int,
        pageSize: Int,
        category: String
    ): List<Product> {
        val response = productService.requestCategoryProducts(category = category)
        return response.content.map { it.toDomain() }
    }
}