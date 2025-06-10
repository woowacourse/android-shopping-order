package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.model.response.ProductDetailResponse.Companion.toDomain
import woowacourse.shopping.data.model.response.ProductsResponse.Content.Companion.toDomain
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepository(
    private val api: ProductApi,
) : ProductRepository {
    override suspend fun fetchProducts(
        page: Int,
        size: Int,
        category: String?,
    ): Products {
        val response = api.getProducts(category, page, size)
        val items = response.content.map { it.toDomain() }
        val pageInfo = Page(page, response.first, response.last)
        return Products(items, pageInfo)
    }

    override suspend fun fetchAllProducts(): List<Product> {
        val firstPage = 0
        val maxSize = Int.MAX_VALUE
        val response = api.getProducts(page = firstPage, size = maxSize)
        return response.content.map { it.toDomain() }
    }

    override suspend fun fetchProduct(productId: Long): ProductDetail = api.getProduct(productId).toDomain()
}
