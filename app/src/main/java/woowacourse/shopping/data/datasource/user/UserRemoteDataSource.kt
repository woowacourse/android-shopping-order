package woowacourse.shopping.data.datasource.user

import woowacourse.shopping.data.model.UserRequest

interface UserRemoteDataSource {

    fun getUser(onReceived: (user: UserRequest) -> Unit)
}
