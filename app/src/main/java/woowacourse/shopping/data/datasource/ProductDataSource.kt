package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductResponse

interface ProductDataSource {
    fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Call<ProductResponse>

    fun getProductById(productId: Int): Call<ProductDto>
}
