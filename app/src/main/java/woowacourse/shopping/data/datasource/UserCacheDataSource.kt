package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.entity.UserEntity

interface UserCacheDataSource {

    fun save(user: UserEntity)

    fun find(): Result<UserEntity>
}
