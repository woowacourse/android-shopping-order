package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Point

interface PointRepository {
    fun getPoint(
        onSuccess: (Point) -> Unit,
        onFailed: (Throwable) -> Unit,
    )
}
