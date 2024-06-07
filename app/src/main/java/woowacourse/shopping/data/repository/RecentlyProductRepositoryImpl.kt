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

    override suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct) {
        recentlyProductDao.addRecentlyProduct(
            recentlyProduct.toRecentlyProductEntity(),
        )
    }

    override suspend fun getMostRecentlyProduct(): Result<RecentlyProduct> {
        var recentlyProduct = RecentlyProduct.defaultRecentlyProduct
        val firstProduct = recentlyProductDao.getMostRecentlyProduct()?.toRecentlyProduct()
        if (firstProduct != null) {
            recentlyProduct = firstProduct
        }
        return Result.runCatching { recentlyProduct }
    }

    override suspend fun getRecentlyProductList(): Result<List<RecentlyProduct>> {
        val pagingData =
            recentlyProductDao.findPagingRecentlyProduct(CURRENT_CART_ITEM_LOAD_PAGING_SIZE)
                .map { it.toRecentlyProduct() }
        return Result.runCatching { pagingData }
    }

    override suspend fun deleteRecentlyProduct(id: Long) {
        val deleteId = recentlyProductDao.deleteRecentlyProductById(id)
        if (deleteId == ERROR_DELETE_DATA_ID) throw NoSuchDataException()
    }

    companion object {
        const val ERROR_DELETE_DATA_ID = 0
        const val CURRENT_CART_ITEM_LOAD_PAGING_SIZE = 10
    }
}
