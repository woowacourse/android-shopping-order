package woowacourse.shopping.data.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.model.ProductDto

class ProductRemoteService {

    private val retrofitService = Retrofit.Builder()
        .baseUrl(ServerInfo.currentBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
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
