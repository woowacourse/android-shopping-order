package woowacourse.shopping.data

import woowacourse.shopping.data.local.DefaultRecentProductDataSource
import woowacourse.shopping.data.local.db.AppDatabase
import woowacourse.shopping.data.remote.RetrofitDataSource
import woowacourse.shopping.domain.Repository

object RepositoryInjector {
    var repository: Repository =
        RepositoryImpl(
            DefaultRecentProductDataSource(AppDatabase.instance.recentProductDao()),
            RetrofitDataSource(),
        )
        private set

    fun setInstance(repository: Repository) {
        RepositoryInjector.repository = repository
    }
}
