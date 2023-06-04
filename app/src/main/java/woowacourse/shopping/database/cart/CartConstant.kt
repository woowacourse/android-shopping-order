package woowacourse.shopping.database.cart

import android.database.Cursor
import android.provider.BaseColumns
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.Product

object CartConstant : BaseColumns {
    private const val TABLE_NAME = "cart_products"
    private const val TABLE_COLUMN_ID = "product_id"
    private const val TABLE_COLUMN_NAME = "product_name"
    private const val TABLE_COLUMN_COUNT = "product_count"
    private const val TABLE_COLUMN_CHECK = "product_check"
    private const val TABLE_COLUMN_PRICE = "product_price"
    private const val TABLE_COLUMN_IMAGE_URL = "product_img_url"
    private const val TABLE_COLUMN_PRODUCT_ID = "product_product_id"
    private const val TABLE_COLUMN_SAVE_TIME = "product_save_time"

    fun getCreateTableQuery(): String {
        return """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $TABLE_COLUMN_ID INTEGER PRIMARY KEY,
                $TABLE_COLUMN_NAME TEXT,
                $TABLE_COLUMN_COUNT INTEGER,
                $TABLE_COLUMN_PRICE INTEGER,
                $TABLE_COLUMN_CHECK INTEGER,
                $TABLE_COLUMN_IMAGE_URL TEXT,
                $TABLE_COLUMN_SAVE_TIME INTEGER)
        """.trimIndent()
    }

    fun getUpdateTableQuery(): String {
        return "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    fun getPagingQuery(offset: Int, limit: Int): String {
        return "SELECT * FROM $TABLE_NAME ORDER BY $TABLE_COLUMN_SAVE_TIME DESC LIMIT $offset, $limit"
    }

    fun getGetAllQuery(): String {
        return "SELECT * FROM $TABLE_NAME ORDER BY $TABLE_COLUMN_SAVE_TIME"
    }

    fun fromCursor(cursor: Cursor): CartProduct {
        return CartProduct(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_COLUMN_ID)),
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_COLUMN_COUNT)),
            product = Product(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_COLUMN_PRODUCT_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(TABLE_COLUMN_NAME)),
                price = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_COLUMN_PRICE)),
                imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(TABLE_COLUMN_IMAGE_URL))
            )
        )
    }
}
