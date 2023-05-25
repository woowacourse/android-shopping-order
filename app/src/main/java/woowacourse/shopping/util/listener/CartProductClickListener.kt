package woowacourse.shopping.util.listener

import woowacourse.shopping.model.UiCartProduct

interface CartProductClickListener {
    fun onClickCartProduct(cartProduct: UiCartProduct)
    fun onAddCartProduct(cartProduct: UiCartProduct)
}
