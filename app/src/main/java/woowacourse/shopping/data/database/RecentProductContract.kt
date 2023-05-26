package woowacourse.shopping.data.database

import android.provider.BaseColumns
import woowacourse.shopping.data.model.Server

object RecentProductContract {
    fun getCreateSQL(server: Server): String {
        val tableName = getTableName(server)
        return "CREATE TABLE IF NOT EXISTS $tableName (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${RecentProduct.PRODUCT_ID} INTEGER UNIQUE," +
            "${RecentProduct.CREATE_DATE} TEXT)"
    }

    fun getDropSQL(server: Server): String {
        return "DROP TABLE IF EXISTS ${getTableName(server)}"
    }

    object RecentProduct : BaseColumns {
        const val PRODUCT_ID = "ProductId"
        const val CREATE_DATE = "CreateDate"
    }
}
