package woowacourse.shopping.data.httpclient.service

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.httpclient.response.UserPointInfoResponse

interface UserPointInfoService {
    @GET("/users")
    fun getUserPointInfo(): Call<UserPointInfoResponse>
}
