package woowacourse.shopping.data.datasource.point

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.dto.PointDto
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.service.order.RetrofitPointService
import woowacourse.shopping.domain.model.Point

class PointRemoteDataSource(
    private val pointService: RetrofitPointService,
) : PointDataSource {
    override fun requestPoints(
        onSuccess: (Point) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = pointService.requestPoints()
        call.enqueue(object : retrofit2.Callback<PointDto> {
            override fun onResponse(call: Call<PointDto>, response: Response<PointDto>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit requestPoints result $result")
                    if (result != null) {
                        onSuccess(result.toDomain())
                    }
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<PointDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }
}
