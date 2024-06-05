package woowacourse.shopping.view.products

import woowacourse.shopping.domain.model.product.RecentlyProduct

interface OnClickProducts {
    fun clickProductItem(productId: Long)

    fun clickShoppingCart()

    fun clickLoadPagingData()

    fun clickRecentlyItem(recentlyProduct: RecentlyProduct)
}
