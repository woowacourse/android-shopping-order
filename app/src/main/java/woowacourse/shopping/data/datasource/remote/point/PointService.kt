package woowacourse.shopping.data.datasource.remote.point

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import woowacourse.shopping.data.model.order.PointDto

interface PointService {

    @Headers("Content-Type: application/json")
    @GET("/points")
    fun requestPoints(
        @Header("Authorization") credential: String
    ): Call<PointDto>
}
