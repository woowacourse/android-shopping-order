package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.UserRemoteDataSource
import woowacourse.shopping.data.mapper.toUser
import woowacourse.shopping.ui.model.User

class UserRepositoryImpl(
    private val userRemoteDateSource: UserRemoteDataSource,
) : UserRepository {

    override fun getUser(onReceived: (user: User) -> Unit) {
        userRemoteDateSource.getUser { dataUser ->
            onReceived(dataUser.toUser())
        }
    }
}
