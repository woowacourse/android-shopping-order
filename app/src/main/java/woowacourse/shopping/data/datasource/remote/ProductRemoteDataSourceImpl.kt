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
    ): Result<List<Product>> =
        handleApiCall(
            errorMessage = "상품 목록 조회 실패",
        ) {
            val response = productService.requestProducts(page, pageSize, category)
            if (!response.isSuccessful) {
                throw Exception("API 호출 실패: ${response.code()} ${response.message()}")
            }
            response.body()?.content?.map { it.toDomain() }
                ?: throw IllegalStateException("응답 바디가 null입니다.")
        }

    override suspend fun fetchProductById(id: Long): Result<Product> =
        handleApiCall(
            errorMessage = "Id로 상품 조회 실패",
        ) {
            val response = productService.requestProductById(id)
            if (!response.isSuccessful) {
                throw Exception("API 호출 실패: ${response.code()} ${response.message()}")
            }
            response.body()?.toDomain() ?: throw IllegalStateException("응답 바디가 null입니다.")
        }
}
