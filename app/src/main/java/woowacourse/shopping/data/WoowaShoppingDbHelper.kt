package woowacourse.shopping.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import woowacourse.shopping.data.WoowaShoppingContract.RecentlyViewed.CREATE_RECENTLY_VIEWED_TABLE
import woowacourse.shopping.data.WoowaShoppingContract.RecentlyViewed.DELETE_RECENTLY_VIEWED_TABLE

class WoowaShoppingDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(PERMIT_FOREIGN_KEY)
        db.execSQL(CREATE_RECENTLY_VIEWED_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DELETE_RECENTLY_VIEWED_TABLE)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "woowaShoppingDatabase"
        private const val PERMIT_FOREIGN_KEY = "PRAGMA foreign_keys=ON"
    }
}
