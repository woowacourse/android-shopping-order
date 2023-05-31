package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.DataEarnRate
import woowacourse.shopping.domain.EarnRate

fun DataEarnRate.toDomain(): EarnRate =
    EarnRate(value)

fun EarnRate.toData(): DataEarnRate =
    DataEarnRate(value)
