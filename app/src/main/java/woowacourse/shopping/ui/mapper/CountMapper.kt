package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Count
import woowacourse.shopping.ui.model.CountUiModel

fun CountUiModel.toCountDomainModel(): Count =
    Count(value)

fun Count.toCountUiModel(): CountUiModel =
    CountUiModel(value)
