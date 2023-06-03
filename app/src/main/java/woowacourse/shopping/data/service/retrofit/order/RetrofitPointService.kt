package woowacourse.shopping.data.service.retrofit.order

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.dto.Point

interface RetrofitPointService {
    @GET("/points")
    fun requestPoints(
        @Header("Authorization") token: String,
    ): Call<Point>
}
