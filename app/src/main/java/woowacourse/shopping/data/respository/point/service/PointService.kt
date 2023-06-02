package woowacourse.shopping.data.respository.point.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity

interface PointService {
    @GET("/points")
    fun requestPoint(
        @Header("Authorization")
        token: String,
    ): Call<PointEntity>

    @GET("/saving-point?")
    fun requestPredictionSavePoint(
        @Query("totalPrice")
        totalPrice: Int,
    ): Call<SavingPointEntity>
}
