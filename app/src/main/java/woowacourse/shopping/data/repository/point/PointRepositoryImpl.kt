package woowacourse.shopping.data.repository.point

import com.example.domain.model.Point
import com.example.domain.repository.PointRepository

class PointRepositoryImpl : PointRepository {
    override fun getPoint(
        onSuccess: (Point) -> Unit,
        onFailure: () -> Unit
    ) {
        onSuccess(Point(3000, 2000))
    }
}
