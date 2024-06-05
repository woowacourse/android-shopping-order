package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.recently.RecentlyProductDatabase
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.utils.EntityMapper.toRecentlyProduct
import woowacourse.shopping.utils.EntityMapper.toRecentlyProductEntity
import woowacourse.shopping.utils.LatchUtils.executeWithLatch
import woowacourse.shopping.utils.exception.NoSuchDataException

class RecentlyProductRepositoryImpl(context: Context) : RecentlyProductRepository {
    private val recentlyProductDao =
        RecentlyProductDatabase.getInstance(context).recentlyProductDao()

    override fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Long> {
        return executeWithLatch {
            recentlyProductDao.addRecentlyProduct(
                recentlyProduct.toRecentlyProductEntity(),
            )
        }
    }

    override fun getMostRecentlyProduct(): Result<RecentlyProduct> {
        return executeWithLatch {
            val firstProduct = recentlyProductDao.getMostRecentlyProduct()?.toRecentlyProduct()
            firstProduct ?: RecentlyProduct.defaultRecentlyProduct
        }
    }

    override fun getRecentlyProductList(): Result<List<RecentlyProduct>> {
        return executeWithLatch {
            recentlyProductDao.findPagingRecentlyProduct(CURRENT_CART_ITEM_LOAD_PAGING_SIZE)
                .map { it.toRecentlyProduct() }
        }
    }

    override fun deleteRecentlyProduct(id: Long): Result<Unit> {
        return executeWithLatch {
            val deleteId = recentlyProductDao.deleteRecentlyProductById(id)
            if (deleteId == ERROR_DELETE_DATA_ID) throw NoSuchDataException()
        }
    }

    companion object {
        const val ERROR_DELETE_DATA_ID = 0
        const val CURRENT_CART_ITEM_LOAD_PAGING_SIZE = 10
    }
}
