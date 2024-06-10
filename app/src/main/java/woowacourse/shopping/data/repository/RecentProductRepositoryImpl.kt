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
        /*{
        var recentProduct: RecentProduct? = null
        recentProduct = dao.loadLatest()?.toDomainModel()
        return recentProduct
    }*/

    override suspend fun loadSecondLatest(): Result<RecentProduct?> =
        runCatching {
            dao.loadSecondLatest()?.toDomainModel()
        }

    /*{
        var secondRecentProduct: RecentProduct? = null
        secondRecentProduct = dao.loadSecondLatest()?.toDomainModel()
        return secondRecentProduct
    }*/

    override suspend fun loadLatestList(): Result<List<RecentProduct>> =
        runCatching {
            dao.loadLatestList().map { it.toDomainModel() }
        }

    /*{
        var recentProducts: List<RecentProduct> = emptyList()
        recentProducts = dao.loadLatestList().map { it.toDomainModel() }
        return recentProducts
    }*/
}
