package woowacourse.shopping.data.local

import woowacourse.shopping.data.datasource.UserCacheDataSource
import woowacourse.shopping.data.entity.UserEntity
import woowacourse.shopping.error.DataError

class UserMemorySource : UserCacheDataSource {
    private lateinit var user: UserEntity

    override fun save(user: UserEntity) {
        this.user = user
    }

    override fun find(): Result<UserEntity> {
        return runCatching {
            if (!::user.isInitialized) throw DataError.UserError("유저가 초기화되지 않았습니다.")
            user
        }
    }
}
