package woowacourse.shopping.data.datasource.user

import woowacourse.shopping.data.datasource.request.UserRequest

interface UserRemoteDataSource {

    fun getUser(onReceived: (user: UserRequest) -> Unit)
}
