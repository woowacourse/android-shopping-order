package woowacourse.shopping.presentation.ui.shoppingCart

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Operator

interface ShoppingCartContract {
    interface View {
        val presenter: Presenter

        fun setShoppingCart(shoppingCart: List<CartProduct>)
        fun setPage(pageNumber: Int)
        fun setPageButtonEnable(previous: Boolean, next: Boolean)
        fun deleteProduct(index: Int)

        fun updateOrder(orderCount: Int)
        fun updatePayment(payment: Int)
        fun updateAllCheck(isChecked: Boolean)
        fun showUnExpectedError()
        fun goProductDetailActivity(cartProduct: CartProduct)
        fun showOrderComplete(orderId: Long)
        fun showChangeLack(lackAmount: Int)
    }

    interface Presenter {
        fun fetchShoppingCart()
        fun setPageNumber()
        fun goNextPage()
        fun goPreviousPage()
        fun checkPageMovement()
        fun deleteProductInCart(index: Int)
        fun changeSelection(index: Int, isSelected: Boolean)
        fun selectAll(isSelected: Boolean)
        fun updateProductQuantity(index: Int, operator: Operator)
        fun applyQuantityChanged(index: Int)
        fun showProductDetail(index: Int)
        fun setOrderCount()
        fun setPayment()
        fun setAllCheck()
        fun requestOrder()
        fun fetchChange()
    }
}
