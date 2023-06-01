package woowacouse.shopping.data.repository.point

import woowacouse.shopping.model.point.Point

interface PointRepository {
    fun loadPoint(
        onFailure: () -> Unit,
        onSuccess: (Point) -> Unit,
    )

    fun loadPredictionSavePoint(
        orderPrice: Int,
        onFailure: () -> Unit,
        onSuccess: (Point) -> Unit,
    )
}
