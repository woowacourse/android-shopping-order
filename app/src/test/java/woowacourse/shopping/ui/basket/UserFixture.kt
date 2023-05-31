package woowacourse.shopping.ui.basket

import woowacourse.shopping.ui.model.User

object UserFixture {

    fun createUser() = User(
        email = "",
        point = 0,
        accumulationRate = 0
    )
}
