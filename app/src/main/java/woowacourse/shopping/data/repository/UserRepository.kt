package woowacourse.shopping.data.repository

import woowacourse.shopping.ui.model.UserUiModel

interface UserRepository {

    fun getUser(onReceived: (user: UserUiModel) -> Unit)
}
