package woowacouse.shopping.data.repository.point

import woowacouse.shopping.model.point.Point

interface PointRepository {
    fun loadPoint(
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit,
    )

    fun loadPredictionSavePoint(
        orderPrice: Int,
        onFailure: (message: String) -> Unit,
        onSuccess: (Point) -> Unit,
    )
}
