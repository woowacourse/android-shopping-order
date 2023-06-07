package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.UserEntity
import woowacourse.shopping.domain.User

fun UserEntity.toDomainModel() = User(
    email = email,
    point = point,
    accumulationRate = accumulationRate
)
