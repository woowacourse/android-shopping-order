package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.ProductResponse

interface RemoteProductDataSource {
    suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): ProductResponse

    suspend fun getProductById(id: Int): Product
}
