package woowacourse.shopping.data.repository.point

import com.example.domain.model.Point
import com.example.domain.repository.PointRepository

class PointRepositoryImpl : PointRepository {
    override fun getPoint(): Point {
        return Point(3000, 2000)
    }
}
