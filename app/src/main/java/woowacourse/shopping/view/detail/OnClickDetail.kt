package woowacourse.shopping.view.detail

import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.RecentlyProduct

interface OnClickDetail {
    fun clickAddCart(product: Product)

    fun clickRecently(recentlyProduct: RecentlyProduct)
}
