package woowacourse.shopping.data.repository.retrofit

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.point.PointDataSource
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.repository.PointRepository

class PointRemoteRepository(
    private val pointDataSource: PointDataSource,
) : PointRepository {
    private val token: String?
        get() = ShoppingApplication.pref.getToken()

    override fun requestPoints(
        onSuccess: (Point) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        pointDataSource.requestPoints(
            token = token!!,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}
