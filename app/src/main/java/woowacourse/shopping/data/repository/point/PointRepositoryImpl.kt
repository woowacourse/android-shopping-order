package woowacourse.shopping.data.repository.point

import com.example.domain.model.FailureInfo
import com.example.domain.model.Point
import com.example.domain.repository.PointRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.RetrofitService
import woowacourse.shopping.data.model.PointDto
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.util.failureInfo

class PointRepositoryImpl : PointRepository {

    private val service = RetrofitService.pointService

    override fun getPoint(
        onSuccess: (Point) -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        service.getPoint().enqueue(object : Callback<PointDto> {
            override fun onResponse(call: Call<PointDto>, response: Response<PointDto>) {
                if (response.code() >= 400) onFailure(failureInfo(response.code()))
                response.body()?.let {
                    onSuccess(it.toDomain())
                }
            }

            override fun onFailure(call: Call<PointDto>, t: Throwable) {
                onFailure(FailureInfo.Default(throwable = t))
            }

        })
    }
}
