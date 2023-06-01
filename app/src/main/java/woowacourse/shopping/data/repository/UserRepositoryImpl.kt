package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.user.UserRemoteDataSource
import woowacourse.shopping.data.mapper.toUserDomainModel
import woowacourse.shopping.domain.User

class UserRepositoryImpl(
    private val userRemoteDateSource: UserRemoteDataSource,
) : UserRepository {

    override fun getUser(onReceived: (user: User) -> Unit) {
        userRemoteDateSource.getUser {
            onReceived(it.toUserDomainModel())
        }
    }
}
