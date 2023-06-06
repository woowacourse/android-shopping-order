package woowacourse.shopping.data.server

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.Storage

class ShoppingRetrofit private constructor() {
    companion object {
        @Volatile
        private var instance: Retrofit? = null

        fun getInstance(storage: Storage): Retrofit {
            return instance ?: synchronized(this) {
                instance ?: createRetrofit(storage)
            }
        }

        private fun createRetrofit(storage: Storage): Retrofit {
            val interceptorClient = OkHttpClient().newBuilder()
                .addInterceptor(AuthorizationInterceptor(storage.credential))
                .build()

            return Retrofit.Builder()
                .baseUrl(Server.getUrl(storage.server))
                .client(interceptorClient)
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
                .also { instance = it }
        }

        fun releaseInstance() {
            instance = null
        }
    }
}