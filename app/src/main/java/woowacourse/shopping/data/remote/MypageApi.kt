package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.*
import woowacourse.shopping.data.remote.dto.TotalCashDTO

interface MypageApi {
    @GET("/members/cash")
    fun requestCash(): Call<TotalCashDTO>

    @POST("/members/cash")
    fun requestChargeCash(@Body cashToCharge: Int): Call<TotalCashDTO>
}
