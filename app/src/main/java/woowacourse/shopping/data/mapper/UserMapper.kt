package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.request.UserRequest
import woowacourse.shopping.domain.User

fun UserRequest.toUserDomainModel() = User(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
