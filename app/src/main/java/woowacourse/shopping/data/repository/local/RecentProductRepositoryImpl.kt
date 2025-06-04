package woowacourse.shopping.data.repository.local

import woowacourse.shopping.data.datasource.local.RecentProductDataSource
import woowacourse.shopping.data.entity.RecentlyViewedProduct
import woowacourse.shopping.data.runThread
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val recentProductDataSource: RecentProductDataSource,
) : RecentProductRepository {
    override fun getRecentProducts(onResult: (Result<List<Product>>) -> Unit) {
        runThread(
            block = {
                recentProductDataSource
                    .getProducts()
                    .map {
                        it.toDomain()
                    }
            },
            onResult = onResult,
        )
    }

    override fun getMostRecentProduct(onResult: (Result<Product?>) -> Unit) {
        runThread(
            block = {
                recentProductDataSource.getMostRecentProduct()?.toDomain()
            },
            onResult = onResult,
        )
    }

    override fun insertRecentProduct(
        product: Product,
        onResult: (Result<Unit>) -> Unit,
    ) {
        runThread(
            block = {
                val recentProducts = recentProductDataSource.getProducts()
                val productId = product.productId

                if (isNewProduct(recentProducts, productId) && recentProducts.size == 10) {
                    val oldProduct = recentProductDataSource.getOldestProduct()
                    recentProductDataSource.delete(oldProduct)
                }

                recentProductDataSource.insert(product.toEntity())
                Result.success(Unit)
            },
            onResult = onResult,
        )
    }

    private fun isNewProduct(
        recentProducts: List<RecentlyViewedProduct>,
        productId: Long,
    ): Boolean = recentProducts.none { it.productId == productId }

    private fun RecentlyViewedProduct.toDomain(): Product =
        Product(
            productId = this.productId,
            name = this.name,
            _price = Price(this.price),
            imageUrl = this.imageUrl,
            category = this.category,
        )

    private fun Product.toEntity(): RecentlyViewedProduct =
        RecentlyViewedProduct(
            productId = this.productId,
            viewedAt = System.currentTimeMillis(),
            name = this.name,
            price = this.price,
            imageUrl = this.imageUrl,
            category = this.category,
        )
}
