package woowacourse.shopping.data.remote.shopping

import retrofit2.Call
import woowacourse.shopping.data.remote.shopping.dto.ProductDto
import woowacourse.shopping.data.remote.shopping.dto.ProductResponse

interface ProductApiService {
    fun loadById(productId: Long): Call<ProductDto>

    fun load(
        startPage: Int,
        pageSize: Int,
    ): Call<ProductResponse>
}
