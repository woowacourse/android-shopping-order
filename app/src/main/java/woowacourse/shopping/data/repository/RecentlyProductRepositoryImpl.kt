package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.recently.RecentlyProductDatabase
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.utils.EntityMapper.toRecentlyProduct
import woowacourse.shopping.utils.EntityMapper.toRecentlyProductEntity
import woowacourse.shopping.utils.exception.ErrorEvent
import kotlin.concurrent.thread

class RecentlyProductRepositoryImpl(context: Context) : RecentlyProductRepository {
    private val recentlyProductDao =
        RecentlyProductDatabase.getInstance(context).recentlyProductDao()

    override fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Unit> {
        thread {
            recentlyProductDao.addRecentlyProduct(
                recentlyProduct.toRecentlyProductEntity(),
            )
        }.join()
        return Result.success(Unit)
    }

    override fun getMostRecentlyProduct(): Result<RecentlyProduct> {
        var recentlyProduct = RecentlyProduct.defaultRecentlyProduct
        thread {
            val firstProduct = recentlyProductDao.getMostRecentlyProduct()?.toRecentlyProduct()
            if (firstProduct != null) {
                recentlyProduct = firstProduct
            }
        }.join()
        return Result.success(recentlyProduct)
    }

    override fun getRecentlyProductList(): Result<List<RecentlyProduct>> {
        var pagingData = emptyList<RecentlyProduct>()
        thread {
            pagingData =
                recentlyProductDao.findPagingRecentlyProduct(CURRENT_CART_ITEM_LOAD_PAGING_SIZE)
                    .map { it.toRecentlyProduct() }
        }.join()
        return Result.success(pagingData)
    }

    override fun deleteRecentlyProduct(id: Long): Result<Unit> {
        var deleteId = ERROR_DELETE_DATA_ID
        thread {
            deleteId = recentlyProductDao.deleteRecentlyProductById(id)
        }.join()
        if (deleteId == ERROR_DELETE_DATA_ID) throw ErrorEvent.LoadDataEvent()
        return Result.success(Unit)
    }

    companion object {
        const val ERROR_DELETE_DATA_ID = 0
        const val CURRENT_CART_ITEM_LOAD_PAGING_SIZE = 10
    }
}
