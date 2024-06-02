package woowacourse.shopping.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private var instance: Retrofit? = null
    private const val BASE_URL = "http://54.180.95.212:8080"

    private var username: String = "ii2001"
    private var password: String = "password"

    private val credentialsProvider =
        object : CredentialsProvider {
            override fun getUsername(): String = username

            override fun getPassword(): String = password
        }

    // 계정정보 업데이트시 호출
    fun updateCredentials(
        newUsername: String,
        newPassword: String,
    ) {
        username = newUsername
        password = newPassword
        instance = null
    }

    private fun getRetrofit(): Retrofit {
        return instance ?: Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(BasicAuthInterceptor(credentialsProvider))
                    .build(),
            )
            .build().also { instance = it }
    }

    fun getApiClient(): ApiClient = RetrofitClient(getRetrofit())

    fun orderService(): OrderApiService = getRetrofit().create(OrderApiService::class.java)
}
