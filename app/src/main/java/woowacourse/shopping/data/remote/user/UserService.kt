package woowacourse.shopping.data.remote.user

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.common.BaseResponse

interface UserService {
    @GET("members/point")
    fun getPoint(): Call<BaseResponse<PointResponse>>
}
