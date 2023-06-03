package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.RecentViewedDBHelper
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentViewedRepository

class RecentViewedDbRepository(
    context: Context,
) : RecentViewedRepository {
    private val dbHelper = RecentViewedDBHelper(context)

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
