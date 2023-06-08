package woowacourse.shopping.data.recentproduct

import com.example.domain.product.Product
import com.example.domain.product.recent.RecentProduct
import java.time.LocalDateTime

interface RecentProductRepository {
    fun getAll(): List<RecentProduct>
    fun getRecentProduct(productId: Int): RecentProduct?
    fun getMostRecentProduct(): RecentProduct?
    fun addRecentProduct(product: Product, viewedDateTime: LocalDateTime)
}
