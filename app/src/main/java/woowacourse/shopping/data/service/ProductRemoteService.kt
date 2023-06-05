package woowacourse.shopping.data.service

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.data.model.ProductDto

class ProductRemoteService {

    private val retrofitService = Retrofit.Builder()
        .baseUrl(ServerInfo.currentBaseUrl)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RetrofitService::class.java)

    fun request(
        onSuccess: (List<ProductDto>) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitService.requestProducts().enqueue(object : retrofit2.Callback<List<ProductDto>> {
            override fun onResponse(
                call: retrofit2.Call<List<ProductDto>>,
                response: retrofit2.Response<List<ProductDto>>
            ) {
                if (response.code() >= 400) return onFailure()
                val value = response.body() ?: emptyList()

                onSuccess(value)
            }

            override fun onFailure(call: retrofit2.Call<List<ProductDto>>, t: Throwable) {
                onFailure()
            }
        })
    }
}
