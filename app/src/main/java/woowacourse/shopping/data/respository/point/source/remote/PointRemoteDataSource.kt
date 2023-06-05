package woowacourse.shopping.data.respository.point.source.remote

import woowacouse.shopping.model.point.Point

interface PointRemoteDataSource {
    fun requestPoint(
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit,
    )

    fun requestPredictionSavePoint(
        orderPrice: Int,
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit,
    )
}
