package woowacourse.shopping.repository

import woowacourse.shopping.User

interface UserRepository {
    fun getUserPoint(onSuccess: (User?) -> Unit)
}
