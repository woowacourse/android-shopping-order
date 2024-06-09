package woowacourse.shopping.data.remote.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.data.remote.dto.response.CouponResponse

interface CouponApi {
    @GET("/coupons")
    suspend fun getCoupons(
        @Header("accept") accept: String = "*/*",
    ): Response<List<CouponResponse>>

    companion object {
        private var service: CouponApi? = null

        fun service(): CouponApi {
            return service ?: RetrofitModule.defaultBuild.create(CouponApi::class.java)
        }
    }
}
