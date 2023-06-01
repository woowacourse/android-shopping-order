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

    override fun findAll(onFinish: (Result<List<UserEntity>>) -> Unit) {
        if (users.isEmpty()) onFinish(Result.failure(IllegalStateException("Empty Cached User")))
        else onFinish(Result.success(users.toList()))
    }
}
