package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.dao.CartProductDao
import woowacourse.shopping.data.local.dao.RecentProductDao
import woowacourse.shopping.data.local.entity.CartEntity
import woowacourse.shopping.data.local.entity.CartProductEntity
import woowacourse.shopping.data.local.entity.RecentEntity
import woowacourse.shopping.data.local.entity.RecentProductEntity

class RoomDataSource(
    private val cartProductDao: CartProductDao,
    private val recentProductDao: RecentProductDao,
) : LocalDataSource {
    override fun findProductByPaging(
        offset: Int,
        pageSize: Int,
    ): List<CartProductEntity> {
        return cartProductDao.findProductByPaging(offset, pageSize)
    }

    override fun findCartByPaging(
        offset: Int,
        pageSize: Int,
    ): List<CartProductEntity> {
        return cartProductDao.findCartByPaging(offset, pageSize)
    }

    override suspend fun findByLimit(limit: Int): List<RecentProductEntity> {
        return recentProductDao.findByLimit(limit)
    }

    override suspend fun findOne(): RecentProductEntity? {
        return recentProductDao.findOne()
    }

    override fun findProductById(id: Long): CartProductEntity? {
        return cartProductDao.findProductById(id)
    }

    override fun saveCart(cartEntity: CartEntity): Long {
        cartProductDao.saveCart(cartEntity)
        return cartEntity.productId
    }

    override fun saveRecent(recentEntity: RecentEntity): Long {
        recentProductDao.saveRecent(recentEntity)
        return recentEntity.productId
    }

    override fun deleteCart(cartId: Long): Long {
        cartProductDao.deleteCart(cartId)
        return cartId
    }

    override fun getMaxCartCount(): Int {
        return cartProductDao.getMaxCartCount()
    }

    override suspend fun saveRecentProduct(recentProductEntity: RecentProductEntity): Long {
        recentProductDao.saveRecentProduct(recentProductEntity)
        return recentProductEntity.productId
    }

    override suspend fun updateRecentProduct(
        productId: Long,
        quantity: Int,
        cartId: Long,
    ) {
        recentProductDao.updateRecentProduct(productId, quantity, cartId)
    }
}
