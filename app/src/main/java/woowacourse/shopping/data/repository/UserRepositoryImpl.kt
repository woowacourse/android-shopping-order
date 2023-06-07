package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.user.UserRemoteDataSource
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.domain.User
import woowacourse.shopping.domain.repository.UserRepository
import java.util.concurrent.CompletableFuture

class UserRepositoryImpl(
    private val userRemoteDateSource: UserRemoteDataSource,
) : UserRepository {

    override fun getUser(): CompletableFuture<Result<User>> {

        return CompletableFuture.supplyAsync {
            userRemoteDateSource.getUser().mapCatching {
                it.toDomainModel()
            }
        }
    }
}
