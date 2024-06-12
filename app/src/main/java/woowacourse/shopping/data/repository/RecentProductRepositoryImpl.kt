package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.entity.mapper
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(context: Context) : RecentProductRepository {
    private val dao = ShoppingDatabase.getInstance(context).recentProductDao()

    override suspend fun save(product: Product) =
        runCatching {
            dao.deleteWithProductId(product.id)
            dao.save(product.mapper())
        }

    override suspend fun loadLatest(): Result<RecentProduct?> =
        runCatching {
            dao.loadLatest()?.toDomainModel()
        }

    override suspend fun loadSecondLatest(): Result<RecentProduct?> =
        runCatching {
            dao.loadSecondLatest()?.toDomainModel()
        }

    override suspend fun loadLatestList(): Result<List<RecentProduct>> =
        runCatching {
            dao.loadLatestList().map { it.toDomainModel() }
        }
}
