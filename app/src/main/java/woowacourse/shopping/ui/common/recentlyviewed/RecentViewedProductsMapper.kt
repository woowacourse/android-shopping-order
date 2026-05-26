package woowacourse.shopping.ui.common.recentlyviewed

import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.recentproduct.RecentProduct

object RecentViewedProductsMapper {
    fun toProducts(
        recentProducts: List<RecentProduct>,
        productsById: Map<Long, Product>,
    ): List<Product> =
        recentProducts.mapNotNull { recentProduct ->
            productsById[recentProduct.productId]
        }

    fun toProduct(
        recentProduct: RecentProduct?,
        productsById: Map<Long, Product>,
    ): Product? = recentProduct?.productId?.let(productsById::get)
}
