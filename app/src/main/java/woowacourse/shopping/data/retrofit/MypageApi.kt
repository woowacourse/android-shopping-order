package woowacourse.shopping.data.retrofit

import retrofit2.Call
import retrofit2.http.*
import woowacourse.shopping.domain.model.TotalCashDTO

interface MypageApi {
    @GET("/members/cash")
    fun requestCash(): Call<TotalCashDTO>

    @POST("/members/cash")
    fun requestChargeCash(@Body cashToCharge: Int): Call<TotalCashDTO>
}
