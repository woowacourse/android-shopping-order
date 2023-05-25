package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.RecentViewedDBHelper
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentViewedRepository

class RecentViewedDbRepository(
    context: Context,
    private val productRepository: ProductRepository,
) : RecentViewedRepository {
    private val dbHelper = RecentViewedDBHelper(context)

    override fun findAll(callBack: (List<Product>) -> Unit) {
        val productIds: List<Int> = dbHelper.selectAll()
        productRepository.getProductsById(productIds, callBack)
    }

    override fun add(id: Int) {
        if (find(id) != null) {
            dbHelper.remove(id)
        }
        dbHelper.insert(id)
    }

    private fun find(id: Int): Int? {
        return dbHelper.selectWhereId(id)
    }

    override fun remove(id: Int) {
        return dbHelper.remove(id)
    }
}
