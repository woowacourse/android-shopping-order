package woowacourse.shopping.ui

import woowacourse.shopping.domain.User

object UserFixture {

    fun createUser() = User(
        email = "",
        point = 0,
        accumulationRate = 0
    )
}
