package woowacourse.shopping.data.remote

import woowacourse.shopping.data.datasource.UserRemoteDataSource
import woowacourse.shopping.data.entity.UserEntity
import woowacourse.shopping.data.remote.retrofit.UserRetrofitService
import woowacourse.shopping.error.DataError

class DefaultUserRemoteSource(private val userService: UserRetrofitService) : UserRemoteDataSource {
    override fun findAll(): Result<List<UserEntity>> {
        return runCatching {
            val response = userService.selectUsers().execute()
            if (response.code() != 200) throw DataError.UserError(response.errorBody()?.string())
            response.body() ?: throw DataError.UserError(response.errorBody()?.string())
        }
    }
}
