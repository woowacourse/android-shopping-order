package woowacourse.shopping.data.remote.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.request.OrderRequestDto
import woowacourse.shopping.data.remote.dto.response.CouponResponseDto

interface CouponApi {
    @GET("/coupons")
    suspend fun getCoupons(
        @Header("accept") accept: String = "*/*",
    ): Response<List<CouponResponseDto>>
}
