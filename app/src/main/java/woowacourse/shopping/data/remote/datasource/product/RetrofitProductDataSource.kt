package woowacourse.shopping.data.remote.datasource.product

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.Message
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.ProductsResponse
import woowacourse.shopping.data.remote.service.ProductApi

class RetrofitProductDataSource(
    private val productApi: ProductApi = ProductApi.service(),
) : ProductDataSource {
    override suspend fun getAllByPaging(
        category: String?,
        page: Int,
        size: Int,
    ): Result<Message<ProductsResponse>> = runCatching {
        val response = productApi.getProducts(category = category, page = page, size = size)
        Message(
            code = response.code(),
            body = response.body()
        )
    }

    override suspend fun post(productRequest: ProductRequest): Result<Message<Unit>> = runCatching {
        val response = productApi.postProduct(productRequest = productRequest)
        Message(
            code = response.code(),
            body = response.body()
        )
    }

    override suspend fun getById(id: Int): Result<Message<ProductResponse>> = runCatching {
        val response = productApi.getProductById(id = id)
        Message(
            code = response.code(),
            body = response.body()
        )
    }

    override suspend fun deleteById(id: Int): Result<Message<Unit>> = runCatching {
        val response = productApi.deleteProductById(id = id)
        Message(
            code = response.code(),
            body = response.body()
        )
    }
}
