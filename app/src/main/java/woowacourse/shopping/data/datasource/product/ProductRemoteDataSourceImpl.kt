package woowacourse.shopping.data.datasource.product

import retrofit2.HttpException
import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.model.response.ProductDetailResponse
import woowacourse.shopping.data.model.response.ProductsResponse

class ProductRemoteDataSourceImpl(
    private val api: ProductApi,
) : ProductRemoteDataSource {
    override suspend fun getProductDetail(productId: Long): ProductDetailResponse {
        val response = api.getProductDetail(productId)
        val body = response.body() ?: throw IllegalStateException()

        return if (response.isSuccessful) body else throw HttpException(response)
    }

    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
    ): ProductsResponse {
        val response = api.getProducts(category, page, size)
        val body = response.body() ?: throw IllegalStateException()

        return if (response.isSuccessful) body else throw HttpException(response)
    }
}
