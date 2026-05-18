package woowacourse.shopping.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import woowacourse.shopping.data.local.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.local.entity.RecentlyViewedProductEntity

val MIGRATION_5_6 =
    object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE `purchase_products`")

            db.execSQL("DROP TABLE `recently_viewed_products`")
            db.execSQL(
                """                
                CREATE TABLE IF NOT EXISTS `recently_viewed_products` (
                    `product_id` INTEGER NOT NULL,
                    `time_stamp` INTEGER NOT NULL,
                    PRIMARY KEY(`product_Id`)
                )
                """.trimIndent(),
            )
        }
    }

@Database(
    entities = [RecentlyViewedProductEntity::class],
    version = 6,
)
abstract class DataBase : RoomDatabase() {
    abstract fun recentlyViewedProductDao(): RecentlyViewedProductDao

    companion object {
        @Volatile
        private var instance: DataBase? = null

        fun getDatabase(context: Context): DataBase {
            val dataBaseContext = context.applicationContext
            return instance ?: synchronized(this) {
                Room
                    .databaseBuilder(
                        context = dataBaseContext,
                        klass = DataBase::class.java,
                        name = "shopping_database",
                    ).addMigrations(MIGRATION_5_6)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
