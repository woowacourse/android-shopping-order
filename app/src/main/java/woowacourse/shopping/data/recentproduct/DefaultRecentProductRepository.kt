package woowacourse.shopping.data.recentproduct

import woowacourse.shopping.data.database.dao.RecentProductDao
import woowacourse.shopping.data.dto.RecentProductEntity
import woowacourse.shopping.data.server.ProductRemoteDataSource
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.RecentProducts
import woowacourse.shopping.domain.repository.RecentProductRepository

class DefaultRecentProductRepository(
    private val recentProductDao: RecentProductDao,
    private val productRemoteDataSource: ProductRemoteDataSource
) : RecentProductRepository {
    override fun getAll(onSuccess: (RecentProducts) -> Unit, onFailure: (String) -> Unit) {
        val recentProducts = mutableListOf<RecentProduct>()
        val entities = recentProductDao.selectAll()
        entities.forEach { entity ->
            productRemoteDataSource.getProduct(
                entity.id,
                onSuccess = { product ->
                    recentProducts.add(RecentProduct(entity.time, product))

                    if (entities.size == recentProducts.size) {
                        onSuccess(RecentProducts(recentProducts))
                    }
                },
                onFailure = { onFailure(MESSAGE_GET_RECENT_PRODUCTS_FAILED) }
            )
        }
    }

    override fun addRecentProduct(recentProduct: RecentProduct) {
        val recentProductEntity = RecentProductEntity(
            recentProduct.product.id,
            recentProduct.time
        )
        recentProductDao.insertRecentProduct(recentProductEntity)
    }

    override fun updateRecentProduct(recentProduct: RecentProduct) {
        val entity = RecentProductEntity(recentProduct.product.id, recentProduct.time)
        recentProductDao.updateRecentProduct(entity)
    }

    override fun getLatestRecentProduct(onSuccess: (RecentProduct?) -> Unit, onFailure: (String) -> Unit) {
        val entity = recentProductDao.selectLatestRecentProduct()
        if (entity == null) {
            onSuccess(null)
        } else {
            productRemoteDataSource.getProduct(
                entity.id,
                onSuccess = { onSuccess(RecentProduct(entity.time, it)) },
                onFailure = { onFailure(MESSAGE_GET_LATEST_RECENT_PRODUCT_FAILED) }
            )
        }
    }

    override fun isExist(id: Int): Boolean {
        return recentProductDao.selectProduct(id) != null
    }

    companion object {
        private const val MESSAGE_GET_RECENT_PRODUCTS_FAILED = "최근 본 상품을 불러오는데 실패했습니다."
        private const val MESSAGE_GET_LATEST_RECENT_PRODUCT_FAILED = "마지막으로 본 상품을 불러오는데 실패했습니다."
    }
}
