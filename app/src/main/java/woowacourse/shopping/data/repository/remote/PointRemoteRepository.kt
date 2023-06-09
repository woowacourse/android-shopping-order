package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.point.PointDataSource
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.repository.PointRepository

class PointRemoteRepository(
    private val pointDataSource: PointDataSource,
) : PointRepository {
    override fun requestPoints(
        onSuccess: (Point) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        pointDataSource.requestPoints(
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}
