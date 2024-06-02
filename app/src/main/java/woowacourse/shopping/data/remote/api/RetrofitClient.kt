package woowacourse.shopping.data.remote.api

import retrofit2.Retrofit

class RetrofitClient(private val retrofit: Retrofit) : ApiClient {
    override fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
}
