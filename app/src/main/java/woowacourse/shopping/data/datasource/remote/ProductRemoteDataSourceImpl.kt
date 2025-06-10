package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.product.PageableResponse
import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.service.ProductService

class ProductRemoteDataSourceImpl(
    private val productService: ProductService,
) : ProductRemoteDataSource {
    override suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): Result<PageableResponse<ProductContent>> =
        handleApiCall(
            errorMessage = "상품 페이지 조회 실패",
            apiCall = { productService.requestProducts(page, pageSize, category) },
            transform = { response ->
                response.body() ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )

    override suspend fun fetchProductById(id: Long): Result<ProductContent> =
        handleApiCall(
            errorMessage = "Id로 상품 조회 실패",
            apiCall = { productService.requestProductById(id) },
            transform = { response ->
                response.body() ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )
}
