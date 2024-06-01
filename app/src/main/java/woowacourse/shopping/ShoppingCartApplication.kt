package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.local.ShoppingCartDataBase
import woowacourse.shopping.data.recent.local.RoomRecentProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class ShoppingCartApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RecentProductRepository.setInstance(RoomRecentProductRepository(ShoppingCartDataBase.instance(this).recentProductDao()))
    }
}
