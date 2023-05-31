package woowacourse.shopping.data.user

import woowacourse.shopping.domain.user.Rank
import woowacourse.shopping.domain.user.User

class UserMemorySource : UserDataSource {
    private val users: MutableSet<User> = mutableSetOf(
        User(0, "a@a.com", "1234", "YUBhLmNvbToxMjM0", Rank.GOLD),
        User(1, "b@b.com", "1234", "YkBiLmNvbToxMjM0", Rank.GOLD),
        User(2, "c@c.com", "1234", "Y0BjLmNvbToxMjM0", Rank.GOLD)
    )

    override fun save(user: User) {
        users.add(user)
    }

    override fun findAll(onFinish: (List<User>) -> Unit) {
        onFinish(users.toList())
    }
}
