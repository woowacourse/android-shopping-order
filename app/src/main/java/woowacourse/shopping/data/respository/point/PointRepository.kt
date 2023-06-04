package woowacourse.shopping.data.respository.point

import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity

interface PointRepository {
    fun requestReservedPoint(
        onFailure: () -> Unit,
        onSuccess: (pointEntity: PointEntity) -> Unit,
    )

    fun requestSavingPoint(
        totalPrice: Int,
        onFailure: () -> Unit,
        onSuccess: (savingPointEntity: SavingPointEntity) -> Unit,
    )
}
