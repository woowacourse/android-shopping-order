package woowacourse.shopping.data.repository.local

import com.example.domain.model.RecentProduct
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.data.service.ProductRemoteService
import woowacourse.shopping.data.sql.recent.RecentDao
import java.time.LocalDateTime
import java.time.ZoneOffset

class RecentProductRepositoryImpl(
    private val recentDao: RecentDao,
    private val mockProductRemoteService: ProductRemoteService,
) : RecentProductRepository {
    override fun getAll(
        onSuccess: (List<RecentProduct>) -> Unit,
        onFailure: () -> Unit,
    ) {
        val recentEntity = recentDao.selectAllRecent()
        val recentProducts: MutableList<RecentProduct> = mutableListOf()
        Thread {
            recentEntity.forEach {
                val product = mockProductRemoteService.requestProduct(it.productId, onFailure)
                val shownDateTime = LocalDateTime.ofEpochSecond(it.dateTimeMills, 0, ZoneOffset.UTC)
                recentProducts.add(RecentProduct(product, shownDateTime))
            }
            onSuccess(recentProducts)
        }.start()
    }

    override fun addRecentProduct(recentProduct: RecentProduct) {
        recentDao.putRecentProduct(recentProduct)
    }
}
