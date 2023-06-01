package woowacourse.shopping.data.retrofit

import retrofit2.Call
import retrofit2.http.*
import woowacourse.shopping.domain.model.TotalCashDTO

interface MypageApi {
    @Headers("Authorization: Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
    @GET("/members/cash")
    fun requestCash(): Call<TotalCashDTO>

    @Headers("Authorization: Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
    @POST("/members/cash")
    fun requestChargeCash(@Body cashToCharge: Int): Call<TotalCashDTO>
}
