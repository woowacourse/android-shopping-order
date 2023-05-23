package woowacourse.shopping.data.recentproduct

import com.example.domain.RecentProduct
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.data.product.MockProductRemoteService
import java.time.LocalDateTime

class RecentProductRepositoryImpl(
    private val productMockProductRemoteService: MockProductRemoteService,
    private val recentProductDao: RecentProductDao
) : RecentProductRepository {

//    private val productRepository: ProductRepository =
//        MockRemoteProductRepositoryImpl(MockProductRemoteService())

    override fun getAll(): List<RecentProduct> {
        return recentProductDao.getAll()
    }

    override fun getMostRecentProduct(): RecentProduct? {
        return recentProductDao.getMostRecentProduct()
    }

    override fun getRecentProduct(productId: Int): RecentProduct? {
        return recentProductDao.getRecentProduct(productId)
    }

    override fun addRecentProduct(productId: Int, viewedDateTime: LocalDateTime) {
        productMockProductRemoteService.requestProduct(
            productId = productId.toLong(),
            onSuccess = { if (it != null) recentProductDao.addColumn(it, viewedDateTime) },
            onFailure = {}
        )
    }
}
