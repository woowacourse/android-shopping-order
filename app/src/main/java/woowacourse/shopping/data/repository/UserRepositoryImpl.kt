package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.user.UserRemoteDataSource
import woowacourse.shopping.data.mapper.toUser
import woowacourse.shopping.ui.model.UserUiModel

class UserRepositoryImpl(
    private val userRemoteDateSource: UserRemoteDataSource,
) : UserRepository {

    override fun getUser(onReceived: (user: UserUiModel) -> Unit) {
        userRemoteDateSource.getUser {
            val user = it.toUser()

            onReceived(user)
        }
    }
}
