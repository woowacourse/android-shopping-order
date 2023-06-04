package woowacourse.shopping.data.respository.point

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity
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

    override fun requestReservedPoint(
        onFailure: () -> Unit,
        onSuccess: (pointEntity: PointEntity) -> Unit,
    ) {
        pointRetrofit.requestReservedPoint("Basic ${Server.TOKEN}")
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

    override fun requestSavingPoint(
        totalPrice: Int,
        onFailure: () -> Unit,
        onSuccess: (pointEntity: SavingPointEntity) -> Unit,
    ) {
        pointRetrofit.requestSavingPoint(totalPrice.toString())
            .enqueue(object : retrofit2.Callback<SavingPointEntity> {
                override fun onResponse(
                    call: Call<SavingPointEntity>,
                    response: Response<SavingPointEntity>,
                ) {
                    if (response.isSuccessful) {
                        val savingPointEntity = response.body() ?: return onFailure()
                        onSuccess(savingPointEntity)
                    }
                }

                override fun onFailure(call: Call<SavingPointEntity>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
