package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.User
import woowacourse.shopping.ui.model.UserUiModel

fun UserUiModel.toDomainModel() = User(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)

fun User.toUiModel() = UserUiModel(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
