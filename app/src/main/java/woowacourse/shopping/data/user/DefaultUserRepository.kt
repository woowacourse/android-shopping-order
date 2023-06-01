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

    override fun findAll(onFinish: (Result<List<User>>) -> Unit) {
        cacheDataSource.findAll { cachedResult ->
            cachedResult.onSuccess { usersEntity ->
                onFinish(Result.success(usersEntity.map { it.toDomain() }))
            }.onFailure {
                findAllRemote(onFinish)
            }
        }
    }

    private fun findAllRemote(onFinish: (Result<List<User>>) -> Unit) {
        remoteDataSource.findAll { remoteResult ->
            remoteResult.onSuccess { remoteUsers ->
                remoteUsers.forEach {
                    cacheDataSource.save(it)
                }
                onFinish(Result.success(remoteUsers.map { it.toDomain() }))
            }.onFailure {
                onFinish(Result.failure(it))
            }
        }
    }

    override fun findCurrent(onFinish: (Result<User>) -> Unit) {
        if (currentUser == null) {
            onFinish(Result.failure(IllegalStateException("User Not Exist!")))
            return
        }
        onFinish(Result.success(currentUser!!))
    }

    companion object {
        private var currentUser: User? = null
    }
}
