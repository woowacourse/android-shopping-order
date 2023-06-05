package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.UserEntity
import woowacourse.shopping.domain.User

fun UserEntity.toUserDomainModel() = User(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
