package woowacourse.shopping.data.discount

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscountRemoteService {

    @GET("discount")
    fun requestDiscountInfo(
        @Query("price") price: Int,
        @Query("memberGrade") memberGrade: String
    ): Call<DiscountInfoDto>
}
