package woowacourse.shopping.data.repository.point

import com.example.domain.model.Point
import com.example.domain.repository.PointRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.point.PointService
import woowacourse.shopping.data.model.PointDto
import woowacourse.shopping.data.model.toDomain

class PointRepositoryImpl(
    private val service: PointService
) : PointRepository {
    override fun getPoint(
        onSuccess: (Point) -> Unit,
        onFailure: () -> Unit
    ) {
        service.getPoint().enqueue(object : Callback<PointDto> {
            override fun onResponse(call: Call<PointDto>, response: Response<PointDto>) {
                if (response.code() >= 400) onFailure()
                response.body()?.let {
                    onSuccess(it.toDomain())
                }
            }

            override fun onFailure(call: Call<PointDto>, t: Throwable) {
                onFailure()
            }

        })
        onSuccess(Point(3000, 2000))
    }
}
