package woowacourse.shopping.data.remote.shopping

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.response.ProductDto
import woowacourse.shopping.data.remote.dto.response.ProductResponse

interface RemoteProductDataSource {
    fun loadById(productId: Long): Call<ProductDto>

    fun load(
        startPage: Int,
        pageSize: Int,
    ): Call<ProductResponse>
}
