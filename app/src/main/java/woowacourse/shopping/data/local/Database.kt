package woowacourse.shopping.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import woowacourse.shopping.data.local.dao.RecentProductDao
import woowacourse.shopping.data.local.entity.RecentProductEntity

@Database(entities = [RecentProductEntity::class], version = 5)
abstract class Database : RoomDatabase() {
    abstract fun recentProductDao(): RecentProductDao
}
