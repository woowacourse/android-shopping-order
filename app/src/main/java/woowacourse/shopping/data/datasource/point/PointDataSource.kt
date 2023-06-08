package woowacourse.shopping.data.datasource.point

import woowacourse.shopping.domain.model.Point

interface PointDataSource {
    fun requestPoints(
        onSuccess: (Point) -> Unit,
        onFailure: (String) -> Unit,
    )
}
