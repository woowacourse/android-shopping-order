package woowacourse.shopping.ui.productDetail

import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener

interface ProductDetailListener : OnItemQuantityChangeListener, OnProductItemClickListener {
    fun navigateToProductDetail(productId: Long) {
        this.onClick(productId)
    }

    override fun onClick(productId: Long) {
        // Do nothing
    }

}
