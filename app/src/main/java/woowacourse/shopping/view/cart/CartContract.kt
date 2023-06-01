package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import woowacourse.shopping.domain.cartsystem.CartSystemResult
import woowacourse.shopping.model.CartProductModel

interface CartContract {
    interface View {
        fun showProducts(items: List<CartViewItem>)
        fun showChangedItems()
        fun showChangedItem(position: Int)
        fun stopLoading()
    }

    interface Presenter {
        val cartSystemResult: LiveData<CartSystemResult>
        val isCheckedAll: LiveData<Boolean>

        fun fetchProducts()
        fun fetchNextPage()
        fun fetchPrevPage()
        fun removeProduct(cartId: Int)
        fun updateCartProductCount(cartId: Int, quantity: Int)
        fun checkProduct(product: CartProductModel)
        fun checkProductsAll()
    }
}
