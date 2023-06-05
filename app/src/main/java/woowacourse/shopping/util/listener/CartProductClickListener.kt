package woowacourse.shopping.util.listener

import woowacourse.shopping.model.CartProductModel

interface CartProductClickListener {
    fun onClickCartProduct(cartProduct: CartProductModel)
    fun onAddCartProduct(cartProduct: CartProductModel)
}
