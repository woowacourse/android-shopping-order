package woowacourse.shopping.data.user

import woowacourse.shopping.data.entity.UserEntity

class UserMemorySource : UserDataSource {
    private val users: MutableSet<UserEntity> = mutableSetOf(
        UserEntity(0, "a@a.com", "1234", "GOLD"),
        UserEntity(1, "b@b.com", "1234", "GOLD"),
        UserEntity(2, "c@c.com", "1234", "GOLD")
    )

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
