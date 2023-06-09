package woowacourse.shopping.data.discount.source

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscountInfoRemoteService {

    @GET("discount")
    fun requestDiscountInfo(
        @Query("price") price: Int,
        @Query("memberGrade") memberGrade: String
    ): Call<NetworkDiscountInfo>
}
