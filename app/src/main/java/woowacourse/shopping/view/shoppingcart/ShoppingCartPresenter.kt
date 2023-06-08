package woowacourse.shopping.view.shoppingcart

import android.widget.TextView
import com.shopping.domain.CartProduct
import com.shopping.repository.CartProductRepository
import woowacourse.shopping.model.Paging
import woowacourse.shopping.model.uimodel.CartProductUIModel
import woowacourse.shopping.model.uimodel.mapper.toDomain
import woowacourse.shopping.model.uimodel.mapper.toUIModel

class ShoppingCartPresenter(
    private val view: ShoppingCartContract.View,
    private val cartProductRepository: CartProductRepository
) : ShoppingCartContract.Presenter {
    override var paging: Paging = Paging(emptyList())

    init {
        cartProductRepository.getAll(
            onSuccess = {
                paging = Paging(it)
                view.updateCartProduct(loadCartProducts())
                setButtonViews()
                view.hideSkeleton()
            },
            onFailure = { throw IllegalStateException("장바구니 상품을 불러오는데 실패하였습니다.") }
        )
    }

    override fun loadCartProducts(): List<CartProductUIModel> =
        paging.loadPageProducts().map { it.toUIModel() }

    override fun removeCartProduct(cartProductUIModel: CartProductUIModel) {
        cartProductRepository.remove(cartProductUIModel.toDomain())
        cartProductRepository.getAll(
            onSuccess = { paging.updateCartProducts(it) },
            onFailure = { throw IllegalStateException("장바구니 상품을 불러오는데 실패하였습니다.") }
        )
        if (paging.isLastIndexOfCurrentPage()) {
            paging.subPage()
            view.updatePageCounter(paging.getPageCount())
        }
        setButtonViews()
        view.updateCartProduct(loadCartProducts())
        updateSelectedTotal()
    }

    override fun loadNextPage(isActivated: Boolean) {
        if (!isActivated) {
            return
        }
        paging.addPage()
        updatePageMove()
    }

    override fun loadPreviousPage(isActivated: Boolean) {
        if (!isActivated) {
            return
        }
        paging.subPage()
        updatePageMove()
    }

    private fun updatePageMove() {
        setButtonViews()
        view.updateCartProduct(loadCartProducts())
        view.updatePageCounter(paging.getPageCount())
        view.updateTotalCheckbox(paging.isAllItemProductSelected())
    }

    override fun updateCartProductCount(cartProductUIModel: CartProductUIModel, tvPrice: TextView) {
        cartProductRepository.update(cartProductUIModel.toDomain())
        view.updateProductItemPrice(cartProductUIModel, tvPrice)
        updateSelectedTotal()
    }

    override fun updateCartProductChecked(cartProductUIModel: CartProductUIModel) {
        cartProductRepository.update(cartProductUIModel.toDomain())
        updateSelectedTotal()
    }

    override fun changeProductsCheckedState(isSelected: Boolean) {
        paging.loadPageProducts().forEach { cartProduct ->
            cartProductRepository.update(
                CartProduct(cartProduct.id, cartProduct.product, cartProduct.count, isSelected)
            )
        }
        updateSelectedTotal()
    }

    override fun updateSelectedTotal() {
        view.updateTotalCheckbox(paging.isAllItemProductSelected())
        view.updateTotalPrice(getTotalPrice())
        view.updateTotalCount(getTotalCount())
    }

    override fun getTotalPrice(): Int =
        cartProductRepository.getTotalPrice()

    override fun getTotalCount(): Int =
        cartProductRepository.getTotalCount()

    override fun getCheckedCartItems() {
        val cartIds = cartProductRepository.getCheckedCartItems()
        view.showPaymentPage(cartIds)
    }

    private fun setButtonViews() {
        if (paging.isPossiblePageUp()) {
            view.activatePageUpCounter()
        } else {
            view.deactivatePageUpCounter()
        }

        if (paging.isPossiblePageDown()) {
            view.activatePageDownCounter()
        } else {
            view.deactivatePageDownCounter()
        }
    }
}
