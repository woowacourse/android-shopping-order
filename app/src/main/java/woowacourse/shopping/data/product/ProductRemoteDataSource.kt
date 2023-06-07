package woowacourse.shopping.data.product

import retrofit2.Call
import woowacourse.shopping.data.common.model.BaseResponse

interface ProductRemoteDataSource {
    fun getProductById(id: Int): Call<BaseResponse<ProductDataModel>>
    fun getAllProducts(): Call<BaseResponse<List<ProductDataModel>>>
}
