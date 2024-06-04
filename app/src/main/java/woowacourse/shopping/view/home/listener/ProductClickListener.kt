package woowacourse.shopping.view.home.listener

import woowacourse.shopping.domain.model.Product

interface ProductClickListener : QuantityClickListener {
    fun onProductClick(productId: Int)

    fun onPlusButtonClick(product: Product)
}
