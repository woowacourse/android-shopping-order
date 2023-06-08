package woowacourse.shopping.data.datasource.remote.point

import com.example.domain.model.point.PointInfo
import woowacourse.shopping.data.datasource.remote.RetrofitService
import woowacourse.shopping.data.model.order.PointDto
import woowacourse.shopping.data.model.toDomain

class PointDataSourceImpl : PointRemoteDataSource {

    private val pointService = RetrofitService.pointService

    override fun loadPoint(
        onSuccess: (PointInfo) -> Unit,
        onFailure: () -> Unit
    ) {
        pointService.requestPoints()
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
