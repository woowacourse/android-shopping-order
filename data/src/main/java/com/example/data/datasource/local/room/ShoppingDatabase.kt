package com.example.data.datasource.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.datasource.local.room.converter.CartItemConverter
import com.example.data.datasource.local.room.dao.CartDao
import com.example.data.datasource.local.room.dao.ProductDao
import com.example.data.datasource.local.room.dao.RecentProductDao
import com.example.data.datasource.local.room.entity.cart.CartItemEntity
import com.example.data.datasource.local.room.entity.product.ProductEntity
import com.example.data.datasource.local.room.entity.recentproduct.RecentProductEntity

@Database(
    entities = [
        CartItemEntity::class,
        ProductEntity::class,
        RecentProductEntity::class,
    ],
    version = 2,
)
@TypeConverters(CartItemConverter::class)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    abstract fun productDao(): ProductDao

    abstract fun recentProductDao(): RecentProductDao

    companion object {
        @Volatile
        private var dbInstance: ShoppingDatabase? = null

        fun getDatabase(context: Context): ShoppingDatabase {
            return dbInstance ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        ShoppingDatabase::class.java,
                        "shopping_database",
                    ).addCallback(
                        object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onOpen(db)
                            }
                        },
                    ).fallbackToDestructiveMigration().build()
                dbInstance = instance
                instance
            }
        }
    }
}
