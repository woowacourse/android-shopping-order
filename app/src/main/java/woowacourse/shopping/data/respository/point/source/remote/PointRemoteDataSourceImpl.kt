package woowacourse.shopping.data.respository.point.source.remote

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.point.service.PointService
import woowacouse.shopping.model.point.Point

class PointRemoteDataSourceImpl(
    url: Server.Url,
    token: Server.Token
) : PointRemoteDataSource {
    private val retrofit = Retrofit.Builder()
        .baseUrl(url.value)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(PointService::class.java)

    private val token = "Basic ${token.value}"

    override fun requestPoint(
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit
    ) {
        retrofit.requestPoint(token).enqueue(object : retrofit2.Callback<PointEntity> {
            override fun onResponse(call: Call<PointEntity>, response: Response<PointEntity>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it.toModel()) } ?: response.errorBody()?.let { onFailure(it.string()) }
                    return
                }
                response.errorBody()?.let { onFailure(it.string()) }
            }

            override fun onFailure(call: Call<PointEntity>, t: Throwable) {
                Log.e("Request Failed", t.toString())
            }
        })
    }

    override fun requestPredictionSavePoint(
        orderPrice: Int,
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit
    ) {
        retrofit.requestPredictionSavePoint(orderPrice)
            .enqueue(object : retrofit2.Callback<SavingPointEntity> {
                override fun onResponse(
                    call: Call<SavingPointEntity>,
                    response: Response<SavingPointEntity>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it.toModel())
                        } ?: response.errorBody()?.let { onFailure(it.string()) }
                        return
                    }
                    response.errorBody()?.let { onFailure(it.string()) }
                }

                override fun onFailure(call: Call<SavingPointEntity>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                }
            })
    }
}
