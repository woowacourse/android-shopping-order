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
            apiCall = { productService.requestProducts(page, pageSize, category) },
            transform = { response ->
                response.body()?.content?.map { it.toDomain() }
                    ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )

    override suspend fun isLastPage(page: Int): Result<Boolean> =
        handleApiCall(
            errorMessage = "마지막 페이지 여부 조회 실패",
            apiCall = { productService.requestProducts(page = page) },
            transform = { response ->
                response.body()?.last ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )

    override suspend fun fetchProductById(id: Long): Result<Product> =
        handleApiCall(
            errorMessage = "Id로 상품 조회 실패",
            apiCall = { productService.requestProductById(id) },
            transform = { response ->
                response.body()?.toDomain() ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )
}
