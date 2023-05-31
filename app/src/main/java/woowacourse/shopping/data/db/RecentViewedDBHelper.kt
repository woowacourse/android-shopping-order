package woowacourse.shopping.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import woowacourse.shopping.domain.model.Product

class RecentViewedDBHelper(context: Context) : SQLiteOpenHelper(context, "recent_viewed", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE ${RecentViewedContract.TABLE_NAME} (" +
                "  ${RecentViewedContract.TABLE_COLUMN_ID} Int PRIMARY KEY not null," +
                "  ${RecentViewedContract.TABLE_COLUMN_NAME} TEXT," +
                "  ${RecentViewedContract.TABLE_COLUMN_IMAGE} TEXT," +
                "  ${RecentViewedContract.TABLE_COLUMN_PRICE} Int" +
                ");",
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${RecentViewedContract.TABLE_NAME}")
        onCreate(db)
    }

    fun insert(product: Product) {
        val values = ContentValues()
        values.put(RecentViewedContract.TABLE_COLUMN_ID, product.id)
        values.put(RecentViewedContract.TABLE_COLUMN_NAME, product.name)
        values.put(RecentViewedContract.TABLE_COLUMN_IMAGE, product.imageUrl)
        writableDatabase.insert(RecentViewedContract.TABLE_NAME, null, values)
    }

    fun remove(id: Int) {
        writableDatabase.execSQL("DELETE FROM ${RecentViewedContract.TABLE_NAME} WHERE ${RecentViewedContract.TABLE_COLUMN_ID}=$id")
    }

    fun selectWhereId(id: Int): Product? {
        val sql =
            "select * from ${RecentViewedContract.TABLE_NAME} WHERE ${RecentViewedContract.TABLE_COLUMN_ID}=$id"
        val cursor = readableDatabase.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val id =
                cursor.getInt(cursor.getColumnIndexOrThrow(RecentViewedContract.TABLE_COLUMN_ID))
            val name =
                cursor.getString(cursor.getColumnIndexOrThrow(RecentViewedContract.TABLE_COLUMN_NAME))
            val imageUrl =
                cursor.getString(cursor.getColumnIndexOrThrow(RecentViewedContract.TABLE_COLUMN_IMAGE))
            val price =
                cursor.getInt(cursor.getColumnIndexOrThrow(RecentViewedContract.TABLE_COLUMN_IMAGE))
            cursor.close()
            return Product(id, name, price, imageUrl)
        }
        return null
    }

    fun selectAll(): List<Product> {
        val viewedProducts = mutableListOf<Product>()
        val sql = "select * from ${RecentViewedContract.TABLE_NAME} order by rowId DESC"
        val cursor = readableDatabase.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val id =
                cursor.getInt(cursor.getColumnIndexOrThrow(RecentViewedContract.TABLE_COLUMN_ID))
            val name =
                cursor.getString(cursor.getColumnIndexOrThrow(RecentViewedContract.TABLE_COLUMN_NAME))
            val imageUrl =
                cursor.getString(cursor.getColumnIndexOrThrow(RecentViewedContract.TABLE_COLUMN_IMAGE))
            val price =
                cursor.getInt(cursor.getColumnIndexOrThrow(RecentViewedContract.TABLE_COLUMN_IMAGE))
            viewedProducts.add(Product(id, name, price, imageUrl))
        }
        cursor.close()
        return viewedProducts
    }

    fun removeOldest() {
        writableDatabase.execSQL("DELETE FROM ${RecentViewedContract.TABLE_NAME} WHERE rowid = (SELECT MIN(rowid) FROM ${RecentViewedContract.TABLE_NAME});")
    }
}
