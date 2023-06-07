package woowacourse.shopping.feature

import com.example.domain.model.Product
import com.example.domain.model.RecentProduct
import java.time.LocalDateTime

object RecentProductFixture {
    fun getRecentProducts(vararg productToDateTime: Pair<Product, LocalDateTime>): List<RecentProduct> {
        return productToDateTime.map { RecentProduct(it.first, it.second) }
    }
}
