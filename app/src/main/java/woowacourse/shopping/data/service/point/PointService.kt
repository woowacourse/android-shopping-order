package woowacourse.shopping.data.service.point

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.dto.PointResponse

interface PointService {
    @GET("/points")
    fun getPoint(): Call<PointResponse>
}
