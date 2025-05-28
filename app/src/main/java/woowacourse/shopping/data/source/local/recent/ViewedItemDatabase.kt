package woowacourse.shopping.data.source.local.recent

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import woowacourse.shopping.data.model.ViewedItem

@Database(entities = [ViewedItem::class], version = 2)
abstract class ViewedItemDatabase : RoomDatabase() {
    abstract fun viewedItemDao(): ViewedItemDao

    companion object {
        @Volatile
        private var instance: ViewedItemDatabase? = null

        fun getInstance(context: Context): ViewedItemDatabase =
            instance ?: synchronized(this) {
                val newInstance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            ViewedItemDatabase::class.java,
                            "viewedItemDatabase",
                        ).build()
                instance = newInstance
                newInstance
            }
    }
}
