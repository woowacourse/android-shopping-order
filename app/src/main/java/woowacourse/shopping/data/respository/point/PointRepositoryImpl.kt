package woowacourse.shopping.data.respository.point

import woowacourse.shopping.data.respository.point.source.remote.PointRemoteDataSource
import woowacouse.shopping.data.repository.point.PointRepository
import woowacouse.shopping.model.point.Point

class PointRepositoryImpl(
    private val pointRemoteDataSource: PointRemoteDataSource
) : PointRepository {
    override fun loadPoint(
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit
    ) {
        pointRemoteDataSource.requestPoint(onFailure, onSuccess)
    }

    override fun loadPredictionSavePoint(
        orderPrice: Int,
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit
    ) {
        pointRemoteDataSource.requestPredictionSavePoint(orderPrice, onFailure, onSuccess)
    }
}
