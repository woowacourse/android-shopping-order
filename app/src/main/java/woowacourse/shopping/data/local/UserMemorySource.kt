package woowacourse.shopping.data.local

import woowacourse.shopping.data.datasource.UserCacheDataSource
import woowacourse.shopping.data.entity.UserEntity

class UserMemorySource : UserCacheDataSource {
    private lateinit var user: UserEntity

    override fun save(user: UserEntity) {
        this.user = user
    }

    override fun find(): Result<UserEntity> {
        return runCatching {
            if (!::user.isInitialized) throw IllegalStateException("Empty Cached User")
            user
        }
    }
}
