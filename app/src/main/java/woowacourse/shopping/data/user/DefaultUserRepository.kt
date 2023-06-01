package woowacourse.shopping.data.user

import woowacourse.shopping.data.entity.UserEntity.Companion.toDomain
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.UserRepository

class DefaultUserRepository(
    private val cacheDataSource: UserDataSource,
    private val remoteDataSource: UserDataSource
) : UserRepository {
    override fun saveCurrent(user: User) {
        currentUser = user
    }

    override fun findAll(): Result<List<User>> {
        return cacheDataSource.findAll().mapCatching { users ->
            users.map { it.toDomain() }
        }.recoverCatching {
            findAllRemote().getOrThrow()
        }
    }

    override fun findCurrent(): Result<User> {
        return runCatching {
            currentUser ?: throw IllegalStateException("User Not Exist!")
        }
    }

    private fun findAllRemote(): Result<List<User>> {
        return remoteDataSource.findAll().mapCatching { remoteUsers ->
            remoteUsers.forEach {
                cacheDataSource.save(it)
            }
            remoteUsers.map { it.toDomain() }
        }
    }

    companion object {
        private var currentUser: User? = null
    }
}
