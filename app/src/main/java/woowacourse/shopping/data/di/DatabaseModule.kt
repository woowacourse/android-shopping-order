package woowacourse.shopping.data.di

import woowacourse.shopping.data.db.PetoMarketDatabase
import woowacourse.shopping.data.db.dao.HistoryDao

class DatabaseModule(
    database: PetoMarketDatabase,
) {
    val historyDao: HistoryDao by lazy { database.historyDao() }
}
