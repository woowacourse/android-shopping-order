package woowacourse.shopping.view.order.listener

import woowacourse.shopping.data.model.Product

interface RecommendClickListener {
    fun onProductClick(productId: Int)

    fun onPlusButtonClick(product: Product)
}
