package woowacourse.shopping.ui.mapper

import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.UserPointInfo
import woowacourse.shopping.ui.model.UiUserPointInfo

fun UiUserPointInfo.toDomain(): UserPointInfo =
    UserPointInfo(
        point = point.toDomain(),
        earnRate = earnRate.toDomain()
    )

fun UserPointInfo.toUi(): UiUserPointInfo =
    UiUserPointInfo(
        point = point.toUi(),
        earnRate = earnRate.toUi()
    )
