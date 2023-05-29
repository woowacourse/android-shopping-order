package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.DataUser
import woowacourse.shopping.ui.model.User

fun DataUser.toUser() = User(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
