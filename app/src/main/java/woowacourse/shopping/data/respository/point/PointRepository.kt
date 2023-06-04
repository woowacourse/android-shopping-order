package woowacourse.shopping.data.respository.point

import woowacourse.shopping.data.model.PointEntity

interface PointRepository {
    fun requestPoint(
        onFailure: () -> Unit,
        onSuccess: (pointEntity: PointEntity) -> Unit,
    )
}
