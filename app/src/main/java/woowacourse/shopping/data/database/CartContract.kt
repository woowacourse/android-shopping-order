package woowacourse.shopping.data.database

import woowacourse.shopping.data.model.Server

object CartContract {
    fun getCreateSQL(url: Server.Url): String {
        val tableName = getTableName(url)
        return "CREATE TABLE IF NOT EXISTS $tableName (" +
            "${Cart.ID} INTEGER PRIMARY KEY," +
            "${Cart.CHECKED} INTEGER)"
    }

    fun getDropSQL(url: Server.Url): String {
        return "DROP TABLE IF EXISTS ${getTableName(url)}"
    }

    object Cart {
        const val ID = "Id"
        const val CHECKED = "Checked"
    }
}
