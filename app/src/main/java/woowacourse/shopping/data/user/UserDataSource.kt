package woowacourse.shopping.data.user

import woowacourse.shopping.data.entity.UserEntity

interface UserDataSource {
    fun save(user: UserEntity)

    fun findAll(onFinish: (Result<List<UserEntity>>) -> Unit)
}
