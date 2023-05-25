package woowacourse.shopping.data.sql.recent

object RecentTableContract {
    const val TABLE_NAME = "recent_table"
    const val TABLE_COLUMN_RECENT_PRODUCT_ID = "recent_product_id"
    const val TABLE_COLUMN_RECENT_IMAGE_URL = "recent_product_image_url"
    const val TABLE_COLUMN_RECENT_NAME = "recent_product_name"
    const val TABLE_COLUMN_RECENT_PRICE = "recent_product_price"
    const val TABLE_COLUMN_DATE_TIME = "recent_date_time"

    fun createSQL(): String {
        return "CREATE TABLE $TABLE_NAME(" +
            "  $TABLE_COLUMN_RECENT_PRODUCT_ID int," +
            "   $TABLE_COLUMN_RECENT_IMAGE_URL string," +
            "   $TABLE_COLUMN_RECENT_NAME string," +
            "   $TABLE_COLUMN_RECENT_PRICE int," +
            "  $TABLE_COLUMN_DATE_TIME int" +
            ");"
    }
}
