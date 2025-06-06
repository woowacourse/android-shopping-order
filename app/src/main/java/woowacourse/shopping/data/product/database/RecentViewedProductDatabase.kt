package woowacourse.shopping.data.product.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import woowacourse.shopping.data.product.LocalDateConverters
import woowacourse.shopping.data.product.dao.RecentViewedProductDao
import woowacourse.shopping.data.product.entity.RecentViewedProductEntity

@Database(entities = [RecentViewedProductEntity::class], version = 1)
@TypeConverters(LocalDateConverters::class)
abstract class RecentViewedProductDatabase : RoomDatabase() {
    abstract fun dao(): RecentViewedProductDao

    companion object {
        @Volatile
        private var instance: RecentViewedProductDatabase? = null

        fun getDataBase(context: Context): RecentViewedProductDatabase =
            instance ?: synchronized(this) {
                val instance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            RecentViewedProductDatabase::class.java,
                            "product_database",
                        ).fallbackToDestructiveMigration(true)
                        .build()
                Companion.instance = instance

                instance
            }
    }
}
