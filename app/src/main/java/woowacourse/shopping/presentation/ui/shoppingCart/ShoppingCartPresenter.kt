package woowacourse.shopping.presentation.ui.shoppingCart

import woowacourse.shopping.domain.model.CartPagination
import woowacourse.shopping.domain.model.Operator
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.util.WoowaResult

class ShoppingCartPresenter(
    private val view: ShoppingCartContract.View,
    private val shoppingCartRepository: ShoppingCartRepository,
) : ShoppingCartContract.Presenter {
    private lateinit var cartPagination: CartPagination

    init {
        shoppingCartRepository.fetchAll { result ->
            when (result) {
                is WoowaResult.SUCCESS -> {
                    cartPagination = CartPagination(result.data)
                    fetchShoppingCart()
                    setPageNumber()
                    checkPageMovement()
                    setOrderCount()
                    setPayment()
                    setAllCheck()
                }
                is WoowaResult.FAIL -> view.showUnExpectedError()
            }
        }
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
        shoppingCartRepository.delete(
            callback = { result ->
                when (result) {
                    is WoowaResult.SUCCESS -> {
                        cartPagination.removeFromCurrentPage(index)
                        checkPageEmpty()
                        updateView()
                    }
                    is WoowaResult.FAIL -> view.showUnExpectedError()
                }
            },
            id = cartPagination[index].cartItem.id,
        )
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
        val callback: (WoowaResult<Boolean>) -> Unit = { result ->
            when (result) {
                is WoowaResult.SUCCESS -> Unit
                is WoowaResult.FAIL -> view.showUnExpectedError()
            }
        }
        shoppingCartRepository.update(
            id = cartPagination[index].cartItem.id,
            updatedQuantity = cartPagination[index].cartItem.quantity,
            callback = callback,
        )
    }

    private fun updateView() {
        fetchShoppingCart()
        setOrderCount()
        setPayment()
        setAllCheck()
        setPageNumber()
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
