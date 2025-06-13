import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.data.model.response.ProductDetailResponse
import woowacourse.shopping.data.model.response.ProductsResponse

class ProductRemoteDataSourceImpl(
    private val api: ProductApi,
) : ProductRemoteDataSource {
    override suspend fun getProductDetail(productId: Long): ApiResult<ProductDetailResponse> {
        val response = api.getProductDetail(productId)
        return when {
            response.isSuccessful ->
                response.body()?.let { ApiResult.Success(it) }
                    ?: ApiResult.UnknownError

            response.code() in 400..499 ->
                ApiResult.ClientError(
                    response.code(),
                    response.message(),
                )

            response.code() >= 500 -> ApiResult.ServerError(response.code(), response.message())
            else -> ApiResult.UnknownError
        }
    }

    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
    ): ApiResult<ProductsResponse> {
        val response = api.getProducts(category, page, size)
        return when {
            response.isSuccessful ->
                response.body()?.let { ApiResult.Success(it) }
                    ?: ApiResult.UnknownError

            response.code() in 400..499 ->
                ApiResult.ClientError(
                    response.code(),
                    response.message(),
                )

            response.code() >= 500 -> ApiResult.ServerError(response.code(), response.message())
            else -> ApiResult.UnknownError
        }
    }
}
