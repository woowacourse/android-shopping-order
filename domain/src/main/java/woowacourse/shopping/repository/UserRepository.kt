package woowacourse.shopping.repository

import woowacourse.shopping.domain.user.User

interface UserRepository {
    fun saveCurrent(user: User)
    fun findAll(): Result<List<User>>
    fun findCurrent(): Result<User>
}
