package woowacourse.shopping.data.sql.recent

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.domain.model.RecentProduct
import woowacourse.shopping.data.model.RecentProductEntity
import java.time.ZoneOffset

class RecentDao(
    context: Context,
) : SQLiteOpenHelper(context, DB_NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(RecentTableContract.createSQL())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${RecentTableContract.TABLE_NAME}")
        onCreate(db)
    }

    fun selectAllRecent(): List<RecentProductEntity> {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM ${RecentTableContract.TABLE_NAME} ORDER BY ${RecentTableContract.TABLE_COLUMN_DATE_TIME} desc LIMIT 10",
            null,
        )

        val recentlyShownProducts = mutableListOf<RecentProductEntity>()
        while (cursor.moveToNext()) {
            val data = RecentProductEntity(
                cursor.getLong(cursor.getColumnIndexOrThrow(RecentTableContract.TABLE_COLUMN_RECENT_PRODUCT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(RecentTableContract.TABLE_COLUMN_RECENT_IMAGE_URL)),
                cursor.getLong(cursor.getColumnIndexOrThrow(RecentTableContract.TABLE_COLUMN_DATE_TIME)),
            )
            recentlyShownProducts.add(data)
        }

        cursor.close()
        return recentlyShownProducts
    }

    private fun selectAll(): List<RecentProductEntity> {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM ${RecentTableContract.TABLE_NAME}",
            null,
        )

        val recentlyShownProducts = mutableListOf<RecentProductEntity>()
        while (cursor.moveToNext()) {
            val data = RecentProductEntity(
                cursor.getLong(cursor.getColumnIndexOrThrow(RecentTableContract.TABLE_COLUMN_RECENT_PRODUCT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(RecentTableContract.TABLE_COLUMN_RECENT_IMAGE_URL)),
                cursor.getLong(cursor.getColumnIndexOrThrow(RecentTableContract.TABLE_COLUMN_DATE_TIME)),
            )
            recentlyShownProducts.add(data)
        }

        cursor.close()
        return recentlyShownProducts
    }

    fun putRecentProduct(recentProduct: RecentProduct) {
        val findRecentProduct = selectAll().find { it.productId == recentProduct.product.id }

        if (findRecentProduct != null) {
            updateRecentProduct(recentProduct)
        } else {
            insertRecentProduct(recentProduct)
        }
    }

    private fun insertRecentProduct(recentProduct: RecentProduct) {
        val timeSecond = recentProduct.dateTime.toEpochSecond(ZoneOffset.UTC)
        val values = ContentValues().apply {
            put(RecentTableContract.TABLE_COLUMN_RECENT_PRODUCT_ID, recentProduct.product.id)
            put(RecentTableContract.TABLE_COLUMN_RECENT_IMAGE_URL, recentProduct.product.imgUrl)
            put(RecentTableContract.TABLE_COLUMN_DATE_TIME, timeSecond)
        }
        writableDatabase.insert(RecentTableContract.TABLE_NAME, null, values)
    }

    private fun updateRecentProduct(recentProduct: RecentProduct) {
        val timeSecond = recentProduct.dateTime.toEpochSecond(ZoneOffset.UTC)
        val updateSql = "UPDATE ${RecentTableContract.TABLE_NAME} " +
            "SET ${RecentTableContract.TABLE_COLUMN_DATE_TIME}=$timeSecond " +
            "WHERE ${RecentTableContract.TABLE_COLUMN_RECENT_PRODUCT_ID}=${recentProduct.product.id}"
        writableDatabase.execSQL(updateSql)
    }

    companion object {
        private const val DB_NAME = "recent_db"
        private const val VERSION = 2
    }
}
