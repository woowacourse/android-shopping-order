package woowacourse.shopping.data.respository.point.source.remote

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.dto.response.PointResponse
import woowacourse.shopping.data.model.dto.response.SavingPointResponse
import woowacourse.shopping.data.respository.point.service.PointService
import woowacouse.shopping.model.point.Point

class PointRemoteDataSourceImpl(
    private val pointService: PointService,
) : PointRemoteDataSource {
    override fun requestPoint(
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit
    ) {
        pointService.requestPoint().enqueue(object : retrofit2.Callback<PointResponse> {
            override fun onResponse(call: Call<PointResponse>, response: Response<PointResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it.toModel()) } ?: response.errorBody()?.let { onFailure(it.string()) }
                    return
                }
                response.errorBody()?.let { onFailure(it.string()) }
            }

            override fun onFailure(call: Call<PointResponse>, t: Throwable) {
                Log.e("Request Failed", t.toString())
                onFailure(ERROR_CONNECT)
            }
        })
    }

    override fun requestPredictionSavePoint(
        orderPrice: Int,
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit
    ) {
        pointService.requestPredictionSavePoint(orderPrice)
            .enqueue(object : retrofit2.Callback<SavingPointResponse> {
                override fun onResponse(
                    call: Call<SavingPointResponse>,
                    response: Response<SavingPointResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it.toModel())
                        } ?: response.errorBody()?.let { onFailure(it.string()) }
                        return
                    }
                    response.errorBody()?.let { onFailure(it.string()) }
                }

                override fun onFailure(call: Call<SavingPointResponse>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                    onFailure(ERROR_CONNECT)
                }
            })
    }

    companion object {
        private const val ERROR_CONNECT = "연결에 실패하였습니다. 다시 시도해주세요"
    }
}
