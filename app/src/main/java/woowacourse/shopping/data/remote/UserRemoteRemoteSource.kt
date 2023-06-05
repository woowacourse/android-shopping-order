package woowacourse.shopping.data.remote

import woowacourse.shopping.data.datasource.UserRemoteDataSource
import woowacourse.shopping.data.entity.UserEntity
import woowacourse.shopping.data.remote.retrofit.UserRetrofitService

class UserRemoteRemoteSource(private val userService: UserRetrofitService) : UserRemoteDataSource {
    override fun findAll(): Result<List<UserEntity>> {
        return runCatching {
            val response = userService.selectUsers().execute()
            if (response.code() != 200) throw Throwable(response.message())
            response.body() ?: throw Throwable(response.message())
        }
    }
}
