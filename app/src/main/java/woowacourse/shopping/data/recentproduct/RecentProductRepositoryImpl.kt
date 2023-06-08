package woowacourse.shopping.data.recentproduct

import android.content.Context
import com.example.domain.product.Product
import com.example.domain.product.recent.RecentProduct
import com.example.domain.product.recent.RecentProductRepository
import woowacourse.shopping.util.BANDAL
import java.time.LocalDateTime

class RecentProductRepositoryImpl(
    context: Context,
    url: String,
    user: String = BANDAL
) : RecentProductRepository {

    private val recentProductDao = RecentProductDao(context.applicationContext, url, user)

    override fun getAll(): List<RecentProduct> {
        return recentProductDao.getAll()
    }

    override fun getMostRecentProduct(): RecentProduct? {
        return recentProductDao.getMostRecentProduct()
    }

    override fun getRecentProduct(productId: Int): RecentProduct? {
        return recentProductDao.getRecentProduct(productId)
    }

    override fun addRecentProduct(product: Product, viewedDateTime: LocalDateTime) {
        recentProductDao.addColumn(product, viewedDateTime)
    }
}
