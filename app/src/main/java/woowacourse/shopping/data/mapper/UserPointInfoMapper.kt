package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.DataUserPointInfo
import woowacourse.shopping.domain.UserPointInfo

fun DataUserPointInfo.toDomain(): UserPointInfo =
    UserPointInfo(
        point = point.toDomain(),
        earnRate = earnRate.toDomain()
    )

fun UserPointInfo.toData(): DataUserPointInfo =
    DataUserPointInfo(
        point = point.toData(),
        earnRate = earnRate.toData()
    )
