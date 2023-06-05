package woowacourse.shopping.data.respository.point.source.remote

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity
import woowacourse.shopping.data.respository.point.service.PointService
import woowacouse.shopping.model.point.Point

class PointRemoteDataSourceImpl(
    private val pointService: PointService,
) : PointRemoteDataSource {
    override fun requestPoint(
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit
    ) {
        pointService.requestPoint().enqueue(object : retrofit2.Callback<PointEntity> {
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
        pointService.requestPredictionSavePoint(orderPrice)
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
