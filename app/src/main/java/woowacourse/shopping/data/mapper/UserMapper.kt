package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.UserResponse
import woowacourse.shopping.domain.User

fun UserResponse.toUserDomainModel() = User(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
