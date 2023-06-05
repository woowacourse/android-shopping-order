package woowacourse.shopping.data.members

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import woowacourse.shopping.utils.ServerConfiguration

interface MemberRemoteService {

    @GET("members")
    fun requestMembers(): Call<List<MemberDto>>

    companion object {
        private val INSTANCE: MemberRemoteService by lazy {
            Retrofit.Builder()
                .baseUrl(ServerConfiguration.host.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MemberRemoteService::class.java)
        }

        fun getInstance(): MemberRemoteService = INSTANCE
    }
}
