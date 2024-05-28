package woowacourse.shopping.data.remote

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.ProductDto
import woowacourse.shopping.data.remote.dto.ProductResponse

interface ProductApiService {
    fun loadById(productId: Long): Call<ProductDto>

    fun load(
        startPage: Int,
        pageSize: Int,
    ): Call<ProductResponse>
}
