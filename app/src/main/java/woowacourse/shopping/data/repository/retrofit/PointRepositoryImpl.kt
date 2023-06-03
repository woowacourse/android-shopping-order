package woowacourse.shopping.data.repository.retrofit

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.point.PointDataSourceImpl
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.repository.PointRepository

class PointRepositoryImpl : PointRepository {
    private val pointDataSource = PointDataSourceImpl()
    private val token: String?
        get() = ShoppingApplication.pref.getToken()

    override fun requestPoints(
        onSuccess: (Point) -> Unit,
        onFailure: () -> Unit,
    ) {
        pointDataSource.requestPoints(
            token = token!!,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}
