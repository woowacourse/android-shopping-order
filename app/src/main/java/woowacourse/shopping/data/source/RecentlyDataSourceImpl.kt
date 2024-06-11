package woowacourse.shopping.data.source

import woowacourse.shopping.data.db.recently.RecentlyProductDao
import woowacourse.shopping.domain.mapper.ProductEntityMapper.toRecentlyProduct
import woowacourse.shopping.domain.mapper.ProductEntityMapper.toRecentlyProductEntity
import woowacourse.shopping.domain.model.product.RecentlyProduct
import woowacourse.shopping.utils.exception.ErrorEvent

class RecentlyDataSourceImpl(
    private val recentlyProductDao: RecentlyProductDao,
) : RecentlyDataSource {
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

    override suspend fun getRecentlyProducts(): Result<List<RecentlyProduct>> {
        return runCatching {
            recentlyProductDao.findPagingRecentlyProduct(CURRENT_CART_ITEM_LOAD_PAGING_SIZE)
                .map { it.toRecentlyProduct() }
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
