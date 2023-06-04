package woowacourse.shopping.data.service

import com.example.domain.model.PointInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.model.PointDto
import woowacourse.shopping.data.model.toDomain

class PointRemoteService(private val credential: String) {

    private val retrofitService = Retrofit.Builder()
        .baseUrl(ServerInfo.currentBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitService::class.java)

    fun loadPoint(
        onSuccess: (PointInfo) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitService.requestPoints("Basic $credential")
            .enqueue(object : retrofit2.Callback<PointDto> {
                override fun onResponse(
                    call: retrofit2.Call<PointDto>,
                    response: retrofit2.Response<PointDto>
                ) {
                    if (response.code() >= 400) return onFailure()
                    val value = response.body()
                    if (value != null) {
                        onSuccess(value.toDomain())
                    }
                }

                override fun onFailure(call: retrofit2.Call<PointDto>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
