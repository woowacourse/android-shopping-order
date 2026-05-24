package woowacourse.shopping.data.remote.api
import retrofit2.http.GET
import woowacourse.shopping.data.remote.dto.CouponResponseDto
interface CouponApi {
    @GET("coupons")
    suspend fun getCoupons(): List<CouponResponseDto>
}
