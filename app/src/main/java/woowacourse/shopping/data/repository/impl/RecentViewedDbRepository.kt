package woowacourse.shopping.data.repository.impl

import android.content.Context
import woowacourse.shopping.data.db.RecentViewedDBHelper
import woowacourse.shopping.data.repository.RecentViewedRepository
import woowacourse.shopping.data.repository.ServerStoreRespository
import woowacourse.shopping.domain.model.Product

class RecentViewedDbRepository(
    context: Context,
    serverRepository: ServerStoreRespository,
) : RecentViewedRepository {
    private val dbHelper = RecentViewedDBHelper(context, serverRepository)

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
