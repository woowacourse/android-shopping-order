package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.request.UserRequest
import woowacourse.shopping.ui.model.User

fun UserRequest.toUser() = User(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
