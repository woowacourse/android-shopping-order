package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.entity.CartEntity
import woowacourse.shopping.data.local.entity.CartProductEntity
import woowacourse.shopping.data.local.entity.RecentEntity
import woowacourse.shopping.data.local.entity.RecentProductEntity

interface LocalDataSource {
    fun findProductByPaging(
        offset: Int,
        pageSize: Int,
    ): List<CartProductEntity>

    fun findCartByPaging(
        offset: Int,
        pageSize: Int,
    ): List<CartProductEntity>

    suspend fun findByLimit(limit: Int): List<RecentProductEntity>

    suspend fun findOne(): RecentProductEntity?

    fun findProductById(id: Long): CartProductEntity?

    fun saveCart(cartEntity: CartEntity): Long

    fun saveRecent(recentEntity: RecentEntity): Long

    fun deleteCart(id: Long): Long

    fun getMaxCartCount(): Int

    suspend fun saveRecentProduct(recentProductEntity: RecentProductEntity): Long

    suspend fun updateRecentProduct(
        productId: Long,
        quantity: Int,
        cartId: Long,
    )
}
