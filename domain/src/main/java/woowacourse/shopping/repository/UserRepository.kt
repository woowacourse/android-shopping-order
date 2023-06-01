package woowacourse.shopping.repository

import woowacourse.shopping.domain.user.User

interface UserRepository {
    fun saveCurrent(user: User)
    fun findAll(onFinish: (Result<List<User>>) -> Unit)
    fun findCurrent(onFinish: (Result<User>) -> Unit)
}
