package woowacourse.shopping.data.mapper

import woowacourse.shopping.Point
import woowacourse.shopping.User
import woowacourse.shopping.data.user.model.UserDataModel

fun UserDataModel.toDomain(): User {
    return User(
        id = memberId,
        point = Point(value = totalPoint)
    )
}
