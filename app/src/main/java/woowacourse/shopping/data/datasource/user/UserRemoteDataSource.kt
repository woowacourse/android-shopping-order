package woowacourse.shopping.data.datasource.user

import woowacourse.shopping.data.datasource.response.UserResponse

interface UserRemoteDataSource {

    fun getUser(
        onReceived: (user: UserResponse) -> Unit,
        onFailure: (errorMessage: String) -> Unit,
    )
}
