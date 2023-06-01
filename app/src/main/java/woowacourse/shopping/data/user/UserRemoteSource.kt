package woowacourse.shopping.data.user

import woowacourse.shopping.data.entity.UserEntity
import woowacourse.shopping.network.retrofit.UserRetrofitService

class UserRemoteSource(private val userService: UserRetrofitService) : UserDataSource {
    override fun save(user: UserEntity) {
    }

    override fun findAll(): Result<List<UserEntity>> {
        val response = userService.selectUsers().execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
            body() ?: throw Throwable(message())
        }
    }
}
