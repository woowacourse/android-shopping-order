package woowacourse.shopping.data.user

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.common.BaseResponse

interface UserService {
    @GET("members/point")
    fun getPoint(
        @Header("Authorization") credentials: String
    ): Call<BaseResponse<PointResponse>>
}
