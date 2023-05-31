package woowacourse.shopping.data.database

import android.provider.BaseColumns
import woowacourse.shopping.data.model.Server

object RecentProductContract {
    fun getCreateSQL(url: Server.Url): String {
        val tableName = getTableName(url)
        return "CREATE TABLE IF NOT EXISTS $tableName (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${RecentProduct.PRODUCT_ID} INTEGER UNIQUE," +
            "${RecentProduct.CREATE_DATE} TEXT)"
    }

    fun getDropSQL(url: Server.Url): String {
        return "DROP TABLE IF EXISTS ${getTableName(url)}"
    }

    object RecentProduct : BaseColumns {
        const val PRODUCT_ID = "ProductId"
        const val CREATE_DATE = "CreateDate"
    }
}
