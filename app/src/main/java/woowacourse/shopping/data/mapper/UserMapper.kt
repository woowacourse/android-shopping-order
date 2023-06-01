package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.request.UserRequest
import woowacourse.shopping.ui.model.UserUiModel

// todo data layer에서 왜 ui를?
fun UserRequest.toUserUiModel() = UserUiModel(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
