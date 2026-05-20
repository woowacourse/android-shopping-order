package woowacourse.shopping.ui.common.recentlyviewed

import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentProduct

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
