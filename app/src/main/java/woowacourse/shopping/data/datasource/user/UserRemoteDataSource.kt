package woowacourse.shopping.data.datasource.user

import woowacourse.shopping.data.datasource.response.UserEntity

interface UserRemoteDataSource {

    fun getUser(): Result<UserEntity>
}
