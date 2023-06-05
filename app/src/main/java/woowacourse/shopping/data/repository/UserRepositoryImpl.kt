package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.user.UserRemoteDataSource
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.domain.User
import woowacourse.shopping.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userRemoteDateSource: UserRemoteDataSource,
) : UserRepository {

    override fun getUser(
        onReceived: (user: User) -> Unit,
        onFailure: (errorMessage: String) -> Unit,
    ) {
        userRemoteDateSource.getUser(
            onReceived = { user ->
                onReceived(user.toDomainModel())
            },
            onFailure = { errorMessage ->
                onFailure(errorMessage)
            }
        )
    }
}
