package woowacourse.shopping.data.recentproduct

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.domain.product.Product
import com.example.domain.product.recent.RecentProduct
import woowacourse.shopping.util.BANDAL
import java.time.LocalDateTime
import java.time.ZoneOffset

class RecentProductDao(
    context: Context,
    private val url: String,
    private val user: String = BANDAL
) {
    private val recentDb: SQLiteDatabase = RecentProductDbHelper(context).writableDatabase

    private fun getCursor(selection: String? = ""): Cursor {
        return recentDb.query(
            RecentProductContract.TABLE_NAME,
            arrayOf(
                RecentProductContract.TABLE_COLUMN_PRODUCT_ID,
                RecentProductContract.TABLE_COLUMN_VIEWED_DATE_TIME,
                RecentProductContract.TABLE_COLUMN_SERVER_URL,
                RecentProductContract.TABLE_COLUMN_USER,
                RecentProductContract.TABLE_COLUMN_PRODUCT_IMAGE_URL,
                RecentProductContract.TABLE_COLUMN_PRODUCT_NAME,
                RecentProductContract.TABLE_COLUMN_PRODUCT_PRICE
            ),
            selection, arrayOf(), null, null, ""
        )
    }

    fun getMostRecentProduct(): RecentProduct? {
        val all = getAll()
        return if (all.isEmpty()) null else all[0]
    }

    fun getRecentProduct(productId: Int): RecentProduct? {
        val cursor = getCursor("${RecentProductContract.TABLE_COLUMN_PRODUCT_ID} = $productId")
        var recentProduct: RecentProduct? = null

        with(cursor) {
            while (moveToNext()) {
                val serverUrl =
                    getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_SERVER_URL))
                val dbUser =
                    getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_USER))

                if (url == serverUrl && user == dbUser)
                    recentProduct = getRecentProduct(this)
            }
        }
        cursor.close()
        return recentProduct
    }

    fun getAll(): List<RecentProduct> {
        val cursor: Cursor = getCursor()
        var recentProducts: MutableList<RecentProduct> = mutableListOf()

        with(cursor) {
            while (moveToNext()) {
                val serverUrl =
                    getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_SERVER_URL))
                val dbUser =
                    getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_USER))

                if (url == serverUrl && user == dbUser)
                    recentProducts.add(getRecentProduct(this))
            }
        }

        cursor.close()
        recentProducts = recentProducts.sortedBy { it.viewedDateTime }.reversed().toMutableList()
        return if (recentProducts.size >= 10) recentProducts.subList(
            0,
            SHOW_COUNT
        ) else recentProducts
    }

    fun addColumn(product: Product, viewedDateTime: LocalDateTime) {
        val deleteQuery =
            """
                DELETE FROM ${RecentProductContract.TABLE_NAME}
                WHERE ${RecentProductContract.TABLE_COLUMN_PRODUCT_ID} = ${product.id};
            """.trimIndent()
        recentDb.execSQL(deleteQuery)

        val values = ContentValues().apply {
            put(
                RecentProductContract.TABLE_COLUMN_VIEWED_DATE_TIME,
                viewedDateTime.toEpochSecond(ZoneOffset.UTC)
            )
            put(RecentProductContract.TABLE_COLUMN_SERVER_URL, url)
            put(RecentProductContract.TABLE_COLUMN_USER, user)
            put(RecentProductContract.TABLE_COLUMN_PRODUCT_ID, product.id)
            put(RecentProductContract.TABLE_COLUMN_PRODUCT_IMAGE_URL, product.imageUrl)
            put(RecentProductContract.TABLE_COLUMN_PRODUCT_NAME, product.name)
            put(RecentProductContract.TABLE_COLUMN_PRODUCT_PRICE, product.price)
        }
        recentDb.insert(RecentProductContract.TABLE_NAME, null, values)
    }

    fun deleteColumn(productId: Int) {
        recentDb.delete(
            RecentProductContract.TABLE_NAME,
            RecentProductContract.TABLE_COLUMN_PRODUCT_ID + "=" + productId, null
        )
    }

    fun createTable() {
        recentDb.execSQL(
            """
                CREATE TABLE ${RecentProductContract.TABLE_NAME} (
                    ${RecentProductContract.TABLE_COLUMN_VIEWED_DATE_TIME} LONG,
                    ${RecentProductContract.TABLE_COLUMN_SERVER_URL} STRING,
                    ${RecentProductContract.TABLE_COLUMN_USER} STRING,
                    ${RecentProductContract.TABLE_COLUMN_PRODUCT_ID} INTEGER,
                    ${RecentProductContract.TABLE_COLUMN_PRODUCT_IMAGE_URL} TEXT,
                    ${RecentProductContract.TABLE_COLUMN_PRODUCT_NAME} TEXT,
                    ${RecentProductContract.TABLE_COLUMN_PRODUCT_PRICE} INTEGER
                )
            """.trimIndent()
        )
    }

    fun deleteTable() {
        recentDb.execSQL(
            """
                DROP TABLE IF EXISTS ${RecentProductContract.TABLE_NAME};
            """.trimIndent()
        )
    }

    private fun getRecentProduct(cursor: Cursor): RecentProduct {
        with(cursor) {
            val productId =
                getInt(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_ID))
            val productImageUrl =
                getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_IMAGE_URL))
            val productName =
                getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_NAME))
            val productPrice =
                getInt(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_PRICE))
            val viewedDateTime =
                getLong(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_VIEWED_DATE_TIME))

            return RecentProduct(
                productId = productId.toLong(),
                productImageUrl = productImageUrl,
                productName = productName,
                productPrice = productPrice,
                viewedDateTime = LocalDateTime.ofEpochSecond(
                    viewedDateTime, 0, ZoneOffset.UTC
                )
            )
        }
    }

    companion object {
        private const val SHOW_COUNT = 10
    }
}
