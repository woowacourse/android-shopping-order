package woowacourse.shopping.data.repository.retrofit

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.point.PointRemoteDataSource
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.repository.PointRepository

class PointRemoteRepository : PointRepository {
    private val pointDataSource = PointRemoteDataSource()
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
