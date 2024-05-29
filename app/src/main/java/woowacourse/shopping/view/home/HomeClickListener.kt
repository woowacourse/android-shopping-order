package woowacourse.shopping.view.home

import woowacourse.shopping.data.model.Product2

interface HomeClickListener {
    fun onProductClick(productId: Int)

    fun onLoadMoreButtonClick()

    fun onShoppingCartButtonClick()

    fun onPlusButtonClick(product: Product2)
}
