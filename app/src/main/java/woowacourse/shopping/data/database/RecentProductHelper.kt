package woowacourse.shopping.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import woowacourse.shopping.data.model.Server

class RecentProductHelper(
    context: Context,
) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        Server.Url.values().forEach { server ->
            db.execSQL(RecentProductContract.getCreateSQL(server))
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Server.Url.values().forEach { server ->
            db.execSQL(RecentProductContract.getDropSQL(server))
        }
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "recentProduct.db"
    }
}
