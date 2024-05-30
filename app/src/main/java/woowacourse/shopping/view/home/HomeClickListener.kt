package woowacourse.shopping.view.home

import woowacourse.shopping.data.model.Product

interface HomeClickListener {
    fun onProductClick(productId: Int)

    fun onLoadMoreButtonClick()

    fun onShoppingCartButtonClick()

    fun onPlusButtonClick(product: Product)
}
