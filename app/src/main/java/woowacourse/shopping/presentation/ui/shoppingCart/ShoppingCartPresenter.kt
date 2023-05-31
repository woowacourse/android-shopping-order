package woowacourse.shopping.presentation.ui.shoppingCart

import android.util.Log
import woowacourse.shopping.domain.model.CartPagination
import woowacourse.shopping.domain.model.Operator
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.util.WoowaResult

class ShoppingCartPresenter(
    private val view: ShoppingCartContract.View,
    private val shoppingCartRepository: ShoppingCartRepository,
) : ShoppingCartContract.Presenter {
    private val cartPagination: CartPagination

    init {
        cartPagination = CartPagination(shoppingCartRepository.getAll())
        Log.d("asdf", "getAll: ${shoppingCartRepository.getAll()}")
        Log.d("asdf", "cartPagination: ${cartPagination.shoppingCart}")
    }

    override fun fetchShoppingCart() {
        view.setShoppingCart(cartPagination.getCurrentPage())
    }

    override fun goNextPage() {
        cartPagination.goNextPage()
        goOtherPage()
    }

    override fun goPreviousPage() {
        cartPagination.goPreviousPage()
        goOtherPage()
    }

    private fun goOtherPage() {
        checkPageMovement()
        setAllCheck()
        setPageNumber()
        fetchShoppingCart()
    }

    override fun setPageNumber() {
        view.setPage(cartPagination.currentPage.value)
    }

    override fun checkPageMovement() {
        view.setPageButtonEnable(
            cartPagination.isPreviousPageEnable(),
            cartPagination.isNextPageEnable(),
        )
    }

    override fun deleteProductInCart(index: Int) {
        val result = shoppingCartRepository.deleteProductInCart(cartPagination[index].product.id)
        if (result) {
            cartPagination.removeFromCurrentPage(index)
            checkPageEmpty()
            updateView()
        }
    }

    private fun checkPageEmpty() {
        if (cartPagination.isCurrentPageEmpty()) goPreviousPage()
    }

    override fun changeSelection(index: Int, isSelected: Boolean) {
        cartPagination.changeCheck(index, isSelected)
        updateView()
    }

    override fun selectAll(isSelected: Boolean) {
        cartPagination.checkAll(isSelected)
        updateView()
    }

    override fun updateProductQuantity(index: Int, operator: Operator) {
        cartPagination.updateProductQuantity(index, operator)
    }

    override fun applyQuantityChanged(index: Int) {
        val result = shoppingCartRepository.updateProductQuantity(
            cartPagination[index].product.id,
            cartPagination[index].quantity,
        )
        when (result) {
            is WoowaResult.SUCCESS -> Unit
            is WoowaResult.FAIL -> {
                view.showUnExpectedError()
            }
        }
    }

    private fun updateView() {
        fetchShoppingCart()
        setOrderCount()
        setPayment()
        setAllCheck()
    }

    override fun setAllCheck() {
        view.updateAllCheck(cartPagination.isAllChecked())
    }

    override fun setPayment() {
        view.updatePayment(cartPagination.getPayment())
    }

    override fun showProductDetail(index: Int) {
        view.goProductDetailActivity(cartPagination[index])
    }

    override fun setOrderCount() {
        view.updateOrder(cartPagination.getOrderCount())
    }
}
