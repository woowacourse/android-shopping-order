package woowacourse.shopping.data.user

import woowacourse.shopping.data.entity.UserEntity
import woowacourse.shopping.network.RetrofitErrorHandlerProvider
import woowacourse.shopping.network.retrofit.UserRetrofitService

class UserRemoteSource(private val userService: UserRetrofitService) : UserDataSource {
    override fun save(user: UserEntity) {
    }

    override fun findAll(onFinish: (Result<List<UserEntity>>) -> Unit) {
        userService.selectUsers()
            .enqueue(RetrofitErrorHandlerProvider.callbackWithBody(200, onFinish))
    }
}
