package woowacourse.shopping.data.source.remote.products

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.ProductsResponse
import woowacourse.shopping.data.source.remote.api.ProductsApiService

class ProductsRemoteDataSource(
    private val api: ProductsApiService,
) : ProductsDataSource {
    override suspend fun getProducts(
        page: Int?,
        size: Int?,
    ): Result<ProductsResponse> =
        runCatching {
            val response = api.getProducts(page = page, size = size)
            if (response.isSuccessful) {
                response.body() ?: ProductsResponse.EMPTY
            } else {
                throw Exception(GET_PRODUCTS_ERROR_MESSAGE)
            }
        }

    override suspend fun getProductById(id: Long): Result<ProductResponse> =
        runCatching {
            val response = api.getProductById(id = id)
            if (response.isSuccessful) {
                response.body() ?: ProductResponse.EMPTY
            } else {
                throw Exception(GET_PRODUCTS_BY_ID_ERROR_MESSAGE)
            }
        }

    override suspend fun getProductsByCategory(category: String): Result<ProductsResponse> =
        runCatching {
            val response = api.getProductsByCategory(category = category)
            if (response.isSuccessful) {
                response.body() ?: ProductsResponse.EMPTY
            } else {
                throw Exception(GET_PRODUCTS_BY_CATEGORY_ERROR_MESSAGE)
            }
        }

    companion object {
        private const val GET_PRODUCTS_ERROR_MESSAGE = "[ERROR] 상품들을 불러올 수 없습니다."
        private const val GET_PRODUCTS_BY_CATEGORY_ERROR_MESSAGE =
            "[ERROR] 카테고리를 바탕으로 상품들을 불러올 수 없습니다."
        private const val GET_PRODUCTS_BY_ID_ERROR_MESSAGE = "[ERROR] id를 바탕으로 상품을 불러올 수 없습니다."
    }
}
