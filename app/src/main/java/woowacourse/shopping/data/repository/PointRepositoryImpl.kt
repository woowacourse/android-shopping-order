package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.PointResponse
import woowacourse.shopping.data.service.point.PointService
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.repository.PointRepository

class PointRepositoryImpl(private val service: PointService) : PointRepository {
    override fun getPoint(
        onSuccess: (Point) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        service.getPoint().enqueue(object : Callback<PointResponse> {
            override fun onResponse(call: Call<PointResponse>, response: Response<PointResponse>) {
                if (response.isSuccessful) {
                    onSuccess(Point(response.body()?.availablePoint ?: 0))
                    return
                }
                onFailed(Throwable(response.message()))
            }

            override fun onFailure(call: Call<PointResponse>, throwable: Throwable) {
                onFailed(throwable)
            }
        })
    }
}
