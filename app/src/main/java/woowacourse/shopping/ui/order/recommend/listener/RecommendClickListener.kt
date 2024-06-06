package woowacourse.shopping.ui.order.recommend.listener

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.home.listener.QuantityClickListener

interface RecommendClickListener : QuantityClickListener {
    fun onProductClick(productId: Int)

    fun onPlusButtonClick(product: Product)
}
