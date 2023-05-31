package woowacourse.shopping.view.productdetail

import androidx.lifecycle.LiveData
import woowacourse.shopping.model.ProductModel

interface ProductDetailContract {
    interface View {
        fun finishActivity(isAdd: Boolean)
        fun showProductDetail(product: ProductModel, lastViewedProduct: ProductModel?)
    }

    interface Presenter {
        val quantity: LiveData<Int>
        fun putInCart(product: ProductModel)
        fun updateRecentViewedProducts(product: ProductModel)
        fun plusCount()
        fun minusCount()
        fun fetchProductDetail()
    }
}
