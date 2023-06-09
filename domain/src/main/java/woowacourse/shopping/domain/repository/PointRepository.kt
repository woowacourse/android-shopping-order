package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Point

interface PointRepository {
    fun requestPoints(
        onSuccess: (Point) -> Unit,
        onFailure: (String) -> Unit,
    )
}
