package woowacourse.shopping.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import woowacourse.shopping.data.local.room.dao.RecentProductDao
import woowacourse.shopping.data.local.room.entity.RecentProductEntity

@Database(entities = [RecentProductEntity::class], version = 5)
abstract class Database : RoomDatabase() {
    abstract fun recentProductDao(): RecentProductDao
}
