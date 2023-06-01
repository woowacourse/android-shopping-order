package woowacourse.shopping.data.product

import retrofit2.Call
import woowacourse.shopping.data.common.BaseResponse

interface ProductDataSource {
    fun getProductById(id: Int): Call<BaseResponse<ProductDataModel>>
    fun getAllProducts(): Call<BaseResponse<List<ProductDataModel>>>
}
