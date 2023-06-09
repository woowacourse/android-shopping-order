package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.UserCacheDataSource
import woowacourse.shopping.data.datasource.UserRemoteDataSource
import woowacourse.shopping.data.entity.UserEntity.Companion.toDomain
import woowacourse.shopping.data.entity.UserEntity.Companion.toEntity
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.UserRepository
import java.util.concurrent.CompletableFuture

class DefaultUserRepository(
    private val cacheDataSource: UserCacheDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {
    override fun saveCurrent(user: User) {
        cacheDataSource.save(user.toEntity())
    }

    override fun findAll(): CompletableFuture<Result<List<User>>> {
        return CompletableFuture.supplyAsync {
            remoteDataSource.findAll().mapCatching { remoteUsers ->
                remoteUsers.forEach {
                    cacheDataSource.save(it)
                }
                remoteUsers.map { it.toDomain() }
            }
        }
    }

    override fun findCurrent(): CompletableFuture<Result<User>> {
        return CompletableFuture.supplyAsync {
            cacheDataSource.find().mapCatching {
                it.toDomain()
            }
        }
    }
}
