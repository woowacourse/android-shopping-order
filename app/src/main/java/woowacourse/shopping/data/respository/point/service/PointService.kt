package woowacourse.shopping.data.respository.point.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import woowacourse.shopping.data.model.dto.response.PointResponse
import woowacourse.shopping.data.model.dto.response.SavingPointResponse

interface PointService {
    @GET("/points")
    fun requestPoint(): Call<PointResponse>

    @GET("/saving-point?")
    fun requestPredictionSavePoint(
        @Query("totalPrice")
        totalPrice: Int,
    ): Call<SavingPointResponse>
}
