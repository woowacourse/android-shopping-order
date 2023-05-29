package woowacourse.shopping.data.recentproduct

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecentProduct::class], version = 1)
abstract class RecentProductDatabase : RoomDatabase() {
    abstract fun recentProductDao(): RecentProductDao

    companion object {
        fun getInstance(context: Context): RecentProductDatabase {
            return Room.databaseBuilder(
                context,
                RecentProductDatabase::class.java,
                "recent_product.db",
            ).build()
        }
    }
}
