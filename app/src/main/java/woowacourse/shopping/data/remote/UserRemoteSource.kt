package woowacourse.shopping.data.remote

import woowacourse.shopping.data.datasource.UserDataSource
import woowacourse.shopping.data.entity.UserEntity
import woowacourse.shopping.data.remote.retrofit.UserRetrofitService

class UserRemoteSource(private val userService: UserRetrofitService) : UserDataSource {
    override fun save(user: UserEntity) {
    }

    override fun findAll(): Result<List<UserEntity>> {
        return runCatching {
            val response = userService.selectUsers().execute()
            if (response.code() != 200) throw Throwable(response.message())
            response.body() ?: throw Throwable(response.message())
        }
    }
}
