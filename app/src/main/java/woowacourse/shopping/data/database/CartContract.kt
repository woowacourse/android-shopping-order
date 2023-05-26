package woowacourse.shopping.data.database

import android.provider.BaseColumns
import woowacourse.shopping.data.model.Server

object CartContract {
    fun getCreateSQL(server: Server): String {
        val tableName = getTableName(server)
        return "CREATE TABLE IF NOT EXISTS $tableName (" +
            "${Cart.ID} INTEGER PRIMARY KEY," +
            "${Cart.CHECKED} INTEGER)"
    }

    fun getDropSQL(server: Server): String {
        return "DROP TABLE IF EXISTS ${getTableName(server)}"
    }

    object Cart : BaseColumns {
        const val ID = "Id"
        const val CHECKED = "Checked"
    }
}
