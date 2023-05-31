package woowacourse.shopping.data.user

import woowacourse.shopping.domain.user.User

interface UserDataSource {
    fun save(user: User)

    fun findAll(onFinish: (List<User>) -> Unit)
}
