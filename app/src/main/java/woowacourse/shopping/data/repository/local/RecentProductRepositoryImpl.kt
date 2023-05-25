package woowacourse.shopping.data.repository.local

import com.example.domain.model.Price
import com.example.domain.model.Product
import com.example.domain.model.RecentProduct
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.data.sql.recent.RecentDao
import java.time.LocalDateTime
import java.time.ZoneOffset

class RecentProductRepositoryImpl(
    private val recentDao: RecentDao,
) : RecentProductRepository {
    override fun getAll(): List<RecentProduct> {
        return recentDao.selectAllRecent().map {
            val product = Product(it.productId, it.name, it.imageUrl, Price(it.price))
            val shownDateTime = LocalDateTime.ofEpochSecond(it.dateTimeMills, 0, ZoneOffset.UTC)
            RecentProduct(product, shownDateTime)
        }
    }

    override fun addRecentProduct(product: Product) {
        recentDao.putRecentProduct(product)
    }
}
