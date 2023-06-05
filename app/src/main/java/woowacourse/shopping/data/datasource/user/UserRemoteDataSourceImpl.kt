package woowacourse.shopping.data.datasource.user

import woowacourse.shopping.data.NetworkModule.AUTHORIZATION_FORMAT
import woowacourse.shopping.data.NetworkModule.encodedUserInfo
import woowacourse.shopping.data.NetworkModule.userService
import woowacourse.shopping.data.datasource.getResult
import woowacourse.shopping.data.datasource.response.UserEntity

class UserRemoteDataSourceImpl : UserRemoteDataSource {

    override fun getUser(): Result<UserEntity> {
        val response = userService.getUser(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo)
        ).execute()

        return response.getResult<UserEntity>(FAILED_TO_GET_USER_INFO)
    }

    companion object {
        private const val FAILED_TO_GET_USER_INFO = "유저 정보를 불러올 수 없습니다"
    }
}
