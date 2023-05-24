package woowacourse.shopping.data.database.table

import woowacourse.shopping.data.database.SqlColumn
import woowacourse.shopping.data.database.SqlType

object SqlRecentProduct : SqlTable {
    const val PRODUCT_ID = "product_id"
    const val TIME = "time"

    override val name: String = "RecentProduct"
    override val scheme: List<SqlColumn> = listOf(
        SqlColumn(PRODUCT_ID, SqlType.INTEGER, "PRIMARY KEY"),
        SqlColumn(TIME, SqlType.TEXT)
    )
}
