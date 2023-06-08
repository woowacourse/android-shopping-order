package woowacourse.shopping.data.cash

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CashRetrofitService {
    @POST("/members/cash")
    fun chargeCash(
        @Body cashToCharge: Int,
    ): Call<TotalCash>

    @GET("/members/cash")
    fun getCash(): Call<TotalCash>
}
