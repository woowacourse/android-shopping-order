package woowacourse.shopping.data.datasource.impl

import android.content.Context
import woowacourse.shopping.data.datasource.RecentViewedDataSource
import woowacourse.shopping.data.db.RecentViewedDBHelper
import woowacourse.shopping.domain.model.Product

class RecentViewedDbDataSource(context: Context, url: String) : RecentViewedDataSource {
    private val dbHelper = RecentViewedDBHelper(context, url)

    override fun findAll(callback: (List<Product>) -> Unit) {
        callback(dbHelper.selectAll())
    }

    override fun add(product: Product) {
        if (find(product.id) != null) {
            dbHelper.remove(product.id)
        }
        dbHelper.insert(product)
    }

    private fun find(id: Int): Product? {
        return dbHelper.selectWhereId(id)
    }

    override fun remove(id: Int) {
        return dbHelper.remove(id)
    }
}
