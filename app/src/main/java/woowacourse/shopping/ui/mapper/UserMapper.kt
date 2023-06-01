package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.User
import woowacourse.shopping.ui.model.UserUiModel

fun UserUiModel.toUserDomainModel() = User(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)

fun User.toUserUiModel() = UserUiModel(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
