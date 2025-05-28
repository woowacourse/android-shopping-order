package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.model.product.ProductResponse

interface ProductRemoteDataSource {
    fun fetchProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Call<PageableResponse<ProductResponse>>

    fun fetchProduct(productId: Int): Call<ProductResponse>
}
