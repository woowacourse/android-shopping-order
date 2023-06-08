package woowacourse.shopping.data.local

import android.provider.BaseColumns

object WoowaShoppingContract : BaseColumns {

    object RecentlyViewed : BaseColumns {
        const val TABLE_NAME = "recentlyViewed"
        const val TABLE_COLUMN_PRODUCT_ID = "productId"
        const val TABLE_COLUMN_VIEWED_DATE_TIME = "viewedDateTime"
        const val TABLE_COLUMN_IMAGE_URL = "imageUrl"
        const val TABLE_COLUMN_NAME = "name"
        const val TABLE_COLUMN_PRICE = "price"
        const val TABLE_COLUMN_SERVER = "server"

        const val CREATE_RECENTLY_VIEWED_TABLE = "CREATE TABLE $TABLE_NAME (" +
            "  ${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "  $TABLE_COLUMN_PRODUCT_ID INTEGER UNIQUE," +
            "  $TABLE_COLUMN_VIEWED_DATE_TIME TEXT," +
            "  $TABLE_COLUMN_IMAGE_URL TEXT," +
            "  $TABLE_COLUMN_NAME TEXT," +
            "  $TABLE_COLUMN_PRICE INTEGER," +
            "  $TABLE_COLUMN_SERVER TEXT" +
            ");"
        const val DELETE_RECENTLY_VIEWED_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}