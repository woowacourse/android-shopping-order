package woowacourse.shopping.data.user

import woowacourse.shopping.data.entity.UserEntity

class UserMemorySource : UserDataSource {
    private val users: MutableSet<UserEntity> = mutableSetOf()

    override fun save(user: UserEntity) {
        users.add(user)
    }

    override fun findAll(): Result<List<UserEntity>> {
        return runCatching {
            if (users.isEmpty()) throw IllegalStateException("Empty Cached User")
            users.toList()
        }
    }
}
