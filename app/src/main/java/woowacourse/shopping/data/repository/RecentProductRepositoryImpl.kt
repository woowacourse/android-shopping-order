package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.database.entity.mapper
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(context: Context) : RecentProductRepository {
    private val dao = ShoppingDatabase.getInstance(context).recentProductDao()

    override suspend fun save(product: Product) {
        dao.deleteWithProductId(product.id)
        dao.save(product.mapper())
    }

    override suspend fun loadLatest(): RecentProduct? {
        return dao.loadLatest()?.toDomainModel()
    }

    override suspend fun loadSecondLatest(): RecentProduct? {
        return dao.loadSecondLatest()?.toDomainModel()
    }

    override suspend fun loadLatestList(): List<RecentProduct> {
        return dao.loadLatestList().map { it.toDomainModel() }
    }
}
