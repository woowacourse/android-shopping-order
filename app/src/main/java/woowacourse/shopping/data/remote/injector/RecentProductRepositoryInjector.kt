package woowacourse.shopping.data.remote.injector

import woowacourse.shopping.data.local.RoomRecentProductDataSource
import woowacourse.shopping.data.local.db.AppDatabase
import woowacourse.shopping.data.remote.repository.RecentProductRepositoryImpl
import woowacourse.shopping.domain.repository.RecentProductRepository

object RecentProductRepositoryInjector {
    var instance: RecentProductRepository =
        RecentProductRepositoryImpl(
            (RoomRecentProductDataSource(AppDatabase.instance.recentProductDao())),
        )
        private set

    fun setInstance(recentProductRepository: RecentProductRepository) {
        instance = recentProductRepository
    }
}
