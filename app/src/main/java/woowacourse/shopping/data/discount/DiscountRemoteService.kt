package woowacourse.shopping.data.discount

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import woowacourse.shopping.utils.ServerConfiguration

interface DiscountRemoteService {

    @GET("discount")
    fun requestDiscountInfo(
        @Query("price") price: Int,
        @Query("memberGrade") memberGrade: String
    ): Call<DiscountInfoDto>

    companion object {
        private val INSTANCE by lazy {
            Retrofit.Builder()
                .baseUrl(ServerConfiguration.host.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DiscountRemoteService::class.java)
        }

        fun getInstance(): DiscountRemoteService = INSTANCE
    }
}
