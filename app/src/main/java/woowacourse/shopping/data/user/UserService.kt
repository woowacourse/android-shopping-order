package woowacourse.shopping.data.user

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.common.BaseResponse

interface UserService {
    @GET("products")
    fun getPoint(): Call<BaseResponse<PointResponse>>
}
