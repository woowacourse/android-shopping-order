package woowacourse.shopping.data.respository.point.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity

interface PointService {
    @GET(PATH_POINT)
    fun requestReservedPoint(): Call<PointEntity>

    @GET(PATH_SAVING_POINT)
    fun requestSavingPoint(
        @Query(PATH_TOTAL_PRICE) totalPrice: String,
    ): Call<SavingPointEntity>

    companion object {
        private const val PATH_TOTAL_PRICE = "totalPrice"
        private const val PATH_POINT = "/points"
        private const val PATH_SAVING_POINT = "/saving-point"
    }
}
