package com.example.data.datasource.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.datasource.local.room.converter.CartItemConverter
import com.example.data.datasource.local.room.converter.RecentProductConverter
import com.example.data.datasource.local.room.dao.CartDao
import com.example.data.datasource.local.room.dao.ProductDao
import com.example.data.datasource.local.room.dao.RecentProductDao
import com.example.data.datasource.local.room.entity.cart.CartItemEntity
import com.example.data.datasource.local.room.entity.product.ProductEntity
import com.example.data.datasource.local.room.entity.recentproduct.RecentProductEntity

@Database(entities = [CartItemEntity::class, ProductEntity::class, RecentProductEntity::class], version = 2)
@TypeConverters(CartItemConverter::class, RecentProductConverter::class)
abstract class ShoppingCartDataBase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    abstract fun productDao(): ProductDao

    abstract fun recentProductDao(): RecentProductDao

    companion object {
        @Volatile
        private var instance: ShoppingCartDataBase? = null

        fun instance(context: Context): ShoppingCartDataBase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(context, ShoppingCartDataBase::class.java, "shopping_cart").build()
                instance = newInstance
                newInstance
            }
        }
    }
}
