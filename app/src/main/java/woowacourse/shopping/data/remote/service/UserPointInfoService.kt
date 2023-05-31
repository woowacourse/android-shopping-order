package woowacourse.shopping.data.remote.service

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.remote.response.UserPointInfoResponse

interface UserPointInfoService {
    @GET("/users")
    fun getUserPointInfo(): Call<UserPointInfoResponse>
}
