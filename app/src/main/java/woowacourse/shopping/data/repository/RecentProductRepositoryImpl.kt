package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.util.runCatchingInThread
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
    private val recentProductLimit: Int = 10,
) : RecentProductRepository {
    override fun getRecentProducts(
        limit: Int,
        onResult: (Result<List<Product>>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val recentProducts = recentProductLocalDataSource.getRecentProducts(limit)
        listOf(
            Product(
                category = "포켓몬",
                id = 921,
                name = "빠모",
                imageUrl = "https://data1.pokemonkorea.co.kr/newdata/pokedex/mid/092101.png",
                price = Price(10_000),
            ),
        )
    }

    override fun insertAndTrimToLimit(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        recentProductLocalDataSource.insertRecentProduct(RecentProductEntity(productId))
        recentProductLocalDataSource.trimToLimit(recentProductLimit)
    }
}
