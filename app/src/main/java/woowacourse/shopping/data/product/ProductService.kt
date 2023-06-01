package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.common.BaseResponse

interface ProductService {
    @GET("products")
    fun getAllProducts(): Call<BaseResponse<List<ProductDataModel>>>

    @GET("products/{id}")
    fun getProductById(
        @Path("id") id: Int,
    ): Call<BaseResponse<ProductDataModel>>
}
