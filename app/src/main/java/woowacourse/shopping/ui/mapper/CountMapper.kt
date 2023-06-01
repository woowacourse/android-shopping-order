package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Count
import woowacourse.shopping.ui.model.CountUiModel

fun CountUiModel.toDomain(): Count =
    Count(value)

fun Count.toUi(): CountUiModel =
    CountUiModel(value)
