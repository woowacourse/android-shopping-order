package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.discount.Point

interface PointRepository {
    fun getPoint(
        onSuccess: (Point) -> Unit,
        onFailed: (Throwable) -> Unit,
    )
}
