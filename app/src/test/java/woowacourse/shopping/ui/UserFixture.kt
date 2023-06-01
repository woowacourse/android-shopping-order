package woowacourse.shopping.ui

import woowacourse.shopping.ui.model.UserUiModel

object UserFixture {

    fun createUser() = UserUiModel(
        email = "",
        point = 0,
        accumulationRate = 0
    )
}
