package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class FakeRecentProductRepository : RecentProductRepository {
    var recentProduct: Product? = null

    override suspend fun getRecentProducts(): Result<List<Product>> = Result.success(ProductsFixture.dummyProducts)

    override suspend fun getMostRecentProduct(): Result<Product?> = Result.success(ProductsFixture.dummyProduct)

    override suspend fun insertRecentProduct(product: Product): Result<Unit> = Result.success(Unit)
}
