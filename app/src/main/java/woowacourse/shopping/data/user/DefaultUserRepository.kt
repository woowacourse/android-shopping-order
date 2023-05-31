package woowacourse.shopping.data.user

import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.UserRepository

class DefaultUserRepository(
    private val cacheDataSource: UserDataSource,
    private val remoteDataSource: UserDataSource
) : UserRepository {
    override fun saveCurrent(user: User) {
        currentUser = user
    }

    override fun findAll(onFinish: (List<User>) -> Unit) {
        cacheDataSource.findAll { cachedUsers ->
            if (cachedUsers.isEmpty()) {
                remoteDataSource.findAll { remoteUsers ->
                    remoteUsers.forEach {
                        cacheDataSource.save(it)
                    }
                    onFinish(remoteUsers)
                }
            } else onFinish(cachedUsers)
        }
    }

    override fun findCurrent(onFinish: (User) -> Unit) {
        onFinish(currentUser)
    }

    companion object {
        private lateinit var currentUser: User
    }
}
