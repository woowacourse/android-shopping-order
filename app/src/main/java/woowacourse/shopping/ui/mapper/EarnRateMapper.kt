package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.EarnRate
import woowacourse.shopping.ui.model.UiEarnRate

fun UiEarnRate.toDomain(): EarnRate =
    EarnRate(value)

fun EarnRate.toUi(): UiEarnRate =
    UiEarnRate(value)
