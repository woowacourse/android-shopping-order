package woowacourse.shopping.data.database.table

import woowacourse.shopping.data.database.SqlColumn
import woowacourse.shopping.data.database.SqlType

object SqlRecentProduct : SqlTable {
    const val PRODUCT_ID = "product_id"
    const val SERVER_NAME = "server_name"
    const val TIME = "time"

    override val name: String = "RecentProduct"
    override val scheme: List<SqlColumn> = listOf(
        SqlColumn(PRODUCT_ID, SqlType.INTEGER),
        SqlColumn(SERVER_NAME, SqlType.TEXT),
        SqlColumn(TIME, SqlType.TEXT)
    )
    override val constraint: String
        get() = ", PRIMARY KEY($PRODUCT_ID, $SERVER_NAME)"
}
