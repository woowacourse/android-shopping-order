package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Count
import woowacourse.shopping.ui.model.CountUiModel

fun CountUiModel.toDomainModel(): Count =
    Count(value)

fun Count.toUiModel(): CountUiModel =
    CountUiModel(value)
