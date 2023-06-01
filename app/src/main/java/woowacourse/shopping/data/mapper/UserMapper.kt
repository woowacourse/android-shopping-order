package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.request.UserRequest
import woowacourse.shopping.ui.model.UserUiModel

fun UserRequest.toUser() = UserUiModel(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
