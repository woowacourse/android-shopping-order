package woowacourse.shopping.data.respository.point

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.presentation.view.util.RetrofitService

class PointRepositoryImpl(
    private val server: Server,
) : PointRepository {

    private val pointRetrofit = Retrofit.Builder()
        .baseUrl(server.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitService::class.java)

    override fun requestPoint(
        onFailure: () -> Unit,
        onSuccess: (pointEntity: PointEntity) -> Unit,
    ) {
        pointRetrofit.requestPoint("Basic ${Server.TOKEN}")
            .enqueue(object : retrofit2.Callback<PointEntity> {
                override fun onResponse(call: Call<PointEntity>, response: Response<PointEntity>) {
                    if (response.isSuccessful) {
                        val pointEntity = response.body() ?: return onFailure()
                        onSuccess(pointEntity)
                    }
                }

                override fun onFailure(call: Call<PointEntity>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
