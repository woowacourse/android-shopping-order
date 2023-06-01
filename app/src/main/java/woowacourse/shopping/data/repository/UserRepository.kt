package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.User

interface UserRepository {

    fun getUser(onReceived: (user: User) -> Unit)
}
