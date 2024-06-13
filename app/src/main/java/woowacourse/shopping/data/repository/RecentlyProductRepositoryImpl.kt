package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.recently.RecentlyProductDatabase
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.utils.EntityMapper.toRecentlyProduct
import woowacourse.shopping.utils.EntityMapper.toRecentlyProductEntity
import woowacourse.shopping.utils.exception.NoSuchDataException

class RecentlyProductRepositoryImpl(context: Context) : RecentlyProductRepository {
    private val recentlyProductDao =
        RecentlyProductDatabase.getInstance(context).recentlyProductDao()

    override suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Long> =
        runCatching {
            recentlyProductDao.addRecentlyProduct(
                recentlyProduct.toRecentlyProductEntity(),
            )
        }

    override suspend fun getMostRecentlyProduct(): Result<RecentlyProduct> =
        runCatching {
            val firstProduct = recentlyProductDao.getMostRecentlyProduct()?.toRecentlyProduct()
            firstProduct ?: RecentlyProduct.defaultRecentlyProduct
        }

    override suspend fun getRecentlyProductList(): Result<List<RecentlyProduct>> =
        runCatching {
            recentlyProductDao.findPagingRecentlyProduct(CURRENT_CART_ITEM_LOAD_PAGING_SIZE)
                .map { it.toRecentlyProduct() }
        }

    override suspend fun deleteRecentlyProduct(id: Long): Result<Unit> =
        runCatching {
            val deleteId = recentlyProductDao.deleteRecentlyProductById(id)
            if (deleteId == ERROR_DELETE_DATA_ID) throw NoSuchDataException()
        }

    companion object {
        const val ERROR_DELETE_DATA_ID = 0
        const val CURRENT_CART_ITEM_LOAD_PAGING_SIZE = 10
    }
}
