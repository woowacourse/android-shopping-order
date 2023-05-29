package woowacourse.shopping.data.repository

import woowacourse.shopping.ui.model.User

interface UserRepository {

    fun getUser(onReceived: (user: User) -> Unit)
}
