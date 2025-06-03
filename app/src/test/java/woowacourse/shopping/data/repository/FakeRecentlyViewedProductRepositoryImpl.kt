package woowacourse.shopping.data.repository

import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.product.catalog.ProductUiModel

class FakeRecentlyViewedProductRepositoryImpl(
    val catalogProductRepository: CatalogProductRepository,
) : RecentlyViewedProductRepository {
    val productIds: LinkedHashSet<Int> = linkedSetOf()

    override fun insertRecentlyViewedProductId(productId: Int) {
        productIds.add(productId)
    }

    override fun getRecentlyViewedProducts(callback: (List<CartProductEntity>) -> Unit) {
        catalogProductRepository.getCartProductsByIds(productIds.toList()) { products ->
            callback(products.map { it.toEntity() })
        }
    }

    override fun getLatestViewedProduct(callback: (ProductUiModel) -> Unit) {
        val lastIndex = listOf(productIds.last)
        catalogProductRepository.getCartProductsByIds(lastIndex) { products ->
            val first = products.first()
            callback(first)
        }
    }
}
