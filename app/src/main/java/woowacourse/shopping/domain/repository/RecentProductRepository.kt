package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import java.lang.IllegalArgumentException

interface RecentProductRepository {
    fun findLastOrNull(): RecentProduct?

    fun findRecentProducts(): List<RecentProduct>

    fun save(product: Product)

    fun getRecommendProducts(cartItems: List<CartItem>): List<Product>

    companion object {
        private const val NOT_INITIALIZE_INSTANCE_MESSAGE = "초기화된 인스턴스가 없습니다."

        @Volatile
        private var instance: RecentProductRepository? = null

        fun setInstance(recentProductRepository: RecentProductRepository) {
            synchronized(this) {
                instance = recentProductRepository
            }
        }

        fun getInstance(): RecentProductRepository {
            return instance ?: throw IllegalArgumentException(NOT_INITIALIZE_INSTANCE_MESSAGE)
        }
    }
}