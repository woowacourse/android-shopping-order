package woowacourse.shopping.feature.cart

import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.PageUiModel

interface CartContract {
    interface View {
        fun changeCartProducts(newItems: List<CartProductUiModel>)
        fun setPageState(hasPrevious: Boolean, hasNext: Boolean, pageNumber: Int)
        fun showPaymentScreen(cartProducts: List<CartProductUiModel>, totalPrice: Int)
    }

    interface Presenter {
        val page: PageUiModel
        fun setup()
        fun deleteCartProduct(cartProduct: CartProductUiModel)
        fun loadPreviousPage()
        fun loadNextPage()
        fun setPage(page: PageUiModel)
        fun increaseCartProduct(cartProduct: CartProductUiModel, previousCount: Int)
        fun decreaseCartProduct(cartProduct: CartProductUiModel, previousCount: Int)
        fun toggleCartProduct(cartProduct: CartProductUiModel, isSelected: Boolean)
        fun toggleAllProductOnPage(isSelected: Boolean)
        fun moveToPayment()
    }
}
