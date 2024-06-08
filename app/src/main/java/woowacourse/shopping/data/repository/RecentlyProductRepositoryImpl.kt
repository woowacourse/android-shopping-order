package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.recently.RecentlyProductDatabase
import woowacourse.shopping.domain.model.product.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.utils.EntityMapper.toRecentlyProduct
import woowacourse.shopping.utils.EntityMapper.toRecentlyProductEntity
import woowacourse.shopping.utils.exception.ErrorEvent

class RecentlyProductRepositoryImpl(context: Context) : RecentlyProductRepository {
    private val recentlyProductDao =
        RecentlyProductDatabase.getInstance(context).recentlyProductDao()

    override suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Unit> {
        return runCatching {
            recentlyProductDao.addRecentlyProduct(
                recentlyProduct.toRecentlyProductEntity(),
            )
        }
    }

    override suspend fun getMostRecentlyProduct(): Result<RecentlyProduct> {
        return runCatching {
            recentlyProductDao.getMostRecentlyProduct()?.toRecentlyProduct()
                ?: throw ErrorEvent.LoadDataEvent()
        }
    }

    override suspend fun getRecentlyProductList(): Result<List<RecentlyProduct>> {
        return runCatching {
            recentlyProductDao.findPagingRecentlyProduct(CURRENT_CART_ITEM_LOAD_PAGING_SIZE)
                .map { it.toRecentlyProduct() }
        }.recoverCatching {
            throw ErrorEvent.LoadDataEvent()
        }
    }

    override suspend fun deleteRecentlyProduct(id: Long): Result<Unit> {
        return runCatching {
            recentlyProductDao.deleteRecentlyProductById(id)
        }
    }

    companion object {
        const val CURRENT_CART_ITEM_LOAD_PAGING_SIZE = 10
    }
}
