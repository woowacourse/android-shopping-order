package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.product.toDomain
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.domain.model.Product

class ProductRemoteDataSourceImpl(
    private val productService: ProductService,
) : ProductRemoteDataSource {
    override suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): List<Product> {
        try {
            val response = productService.requestProducts(page, pageSize, category)
            if (response.content.isEmpty()) {
                throw NoSuchElementException("상품 데이터가 없습니다.")
            }
            return response.content.map { it.toDomain() }
        } catch (e: Exception) {
            throw Exception("인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun fetchProductById(id: Long): Product {
        try {
            return productService.requestProductById(id).toDomain()
        } catch (e: Exception) {
            throw Exception("인터넷 연결을 확인해주세요.")
        }
    }
}
