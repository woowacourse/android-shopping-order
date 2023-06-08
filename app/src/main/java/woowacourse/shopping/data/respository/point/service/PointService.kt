package woowacourse.shopping.data.respository.point.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity

interface PointService {
    @Headers(HEADER_JSON)
    @GET(PATH_POINT)
    fun requestReservedPoint(
        @Header(AUTHORIZATION) token: String,
    ): Call<PointEntity>

    @Headers(HEADER_JSON)
    @GET(PATH_SAVING_POINT)
    fun requestSavingPoint(
        @Query(PATH_TOTAL_PRICE) totalPrice: String,
    ): Call<SavingPointEntity>

    companion object {
        private const val HEADER_JSON = "Content-Type: application/json"

        private const val PATH_TOTAL_PRICE = "totalPrice"
        private const val PATH_POINT = "/points"
        private const val PATH_SAVING_POINT = "/saving-point"

        private const val AUTHORIZATION = "Authorization"
    }
}
