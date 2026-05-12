package woowacourse.shopping._archive.local

import androidx.room.Database
import androidx.room.RoomDatabase
import woowacourse.shopping._archive.local.dao.CartDao
import woowacourse.shopping._archive.local.dao.RecentProductDao
import woowacourse.shopping._archive.local.entity.CartEntity
import woowacourse.shopping._archive.local.entity.RecentProductEntity

@Database(entities = [CartEntity::class, RecentProductEntity::class], version = 3)
abstract class Database : RoomDatabase() {
    abstract fun cartDao(): CartDao

    abstract fun recentProductDao(): RecentProductDao
}
