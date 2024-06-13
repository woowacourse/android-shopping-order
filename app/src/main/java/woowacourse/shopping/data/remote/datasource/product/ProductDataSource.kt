package woowacourse.shopping.data.remote.datasource.product

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.Message
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.ProductsResponse

interface ProductDataSource {
    suspend fun getAllByPaging(
        category: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): Result<Message<ProductsResponse>>

    suspend fun post(productRequest: ProductRequest): Result<Message<Unit>>

    suspend fun getById(id: Int): Result<Message<ProductResponse>>

    suspend fun deleteById(id: Int): Result<Message<Unit>>
}
