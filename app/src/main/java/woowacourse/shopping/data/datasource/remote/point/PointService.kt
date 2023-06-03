package woowacourse.shopping.data.datasource.remote.point

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.model.PointDto

interface PointService {
    @GET("/points")
    fun getPoint(): Call<PointDto>
}
