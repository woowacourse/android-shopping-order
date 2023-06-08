package woowacourse.shopping.data.service.order

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.dto.Point

interface RetrofitPointService {
    @GET("/points")
    fun requestPoints(): Call<Point>
}
