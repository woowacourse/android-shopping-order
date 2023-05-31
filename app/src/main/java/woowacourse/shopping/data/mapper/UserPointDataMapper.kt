package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.DataUserPointData
import woowacourse.shopping.domain.UserPointData

fun DataUserPointData.toDomain(): UserPointData =
    UserPointData(
        point = point.toDomain(),
        earnRate = earnRate.toDomain()
    )

fun UserPointData.toData(): DataUserPointData =
    DataUserPointData(
        point = point.toData(),
        earnRate = earnRate.toData()
    )
