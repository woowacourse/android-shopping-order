package woowacourse.shopping.data

import com.example.domain.model.Point
import com.example.domain.repository.PointRepository
import woowacourse.shopping.data.service.PointRemoteService

class PointRemoteRepositoryImpl(
    private val service: PointRemoteService
) : PointRepository {

    override fun getPoint(
        onSuccess: (Point) -> Unit,
        onFailure: () -> Unit
    ) {
//        service.loadPoint(
//            onSuccess = { onSuccess(it) },
//            onFailure = { onFailure() }
//        )
        onSuccess(Point(1000))
    }
}
