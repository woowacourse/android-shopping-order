package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.entity.UserEntity

interface UserRemoteDataSource {
    fun findAll(): Result<List<UserEntity>>
}
