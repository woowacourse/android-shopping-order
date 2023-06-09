package woowacourse.shopping.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import woowacourse.shopping.data.database.recentProduct.RecentProductConstant

class ShoppingDBHelper(
    context: Context,
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    init {
        onCreate(writableDatabase)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(RecentProductConstant.getCreateTableQuery())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(RecentProductConstant.getUpdateTableQuery())
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 111
        private const val DATABASE_NAME = "shopping_db"
    }
}
