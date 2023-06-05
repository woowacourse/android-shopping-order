package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import woowacourse.shopping.domain.cartsystem.CartSystemResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.model.CartProductModel

interface CartContract {
    interface View {
        fun showProducts(items: List<CartViewItem>)
        fun changeItems(newItems: List<CartViewItem>)
        fun stopLoading()
        fun showOrderActivity(selectedCartProducts: List<CartProduct>)
        fun showProductsNothingToast()

        fun showErrorMessageToast(message: String?)
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
        fun order()
    }
}
