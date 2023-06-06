package woowacourse.shopping.presentation.cart

import woowacourse.shopping.CartProductInfoList
import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.CartProductInfoListModel
import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.model.OrderProductModel
import woowacourse.shopping.presentation.model.OrderProductsModel
import woowacourse.shopping.repository.CartRepository

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
    initPage: Int = DEFAULT_INIT_PAGE,
    initCartProducts: CartProductInfoListModel = CartProductInfoListModel(emptyList()),
) : CartContract.Presenter {

    private val paging = CartOffsetPaging(startPage = initPage)
    private val offset: Int get() = paging.currentPage.getOffset(paging.limit)

    private var cartProducts = CartProductInfoList(initCartProducts.items.map { it.toDomain() })
    private val pageProducts get() = cartProducts.getItemsInRange(offset, paging.limit)

    init {
        view.setPage(paging.currentPage.value.toString())
    }

    override fun loadCartItems() {
        cartRepository.getAllCartItems {
            cartProducts = CartProductInfoList(it)
            view.setLoadingViewVisible(false)
            updateAllState()
        }
    }

    private fun updateAllState() {
        refreshCurrentPageItems()
        checkPageState()
        updateOrder()
    }

    private fun checkPageState() {
        checkPlusPageAble()
        checkMinusPageAble()
    }

    private fun updateOrder() {
        updateOrderPrice()
        updateOrderCount()
        checkCurrentPageProductsIsOrdered()
    }

    override fun checkPlusPageAble() {
        val isPlusAble = paging.isPlusPageAble(cartProducts)
        view.setUpPlusPageState(isPlusAble)
    }

    override fun checkMinusPageAble() {
        val isMinusAble = paging.isMinusPageAble()
        view.setUpMinusPageState(isMinusAble)
    }

    override fun addProductInOrder(cartProductModel: CartProductInfoModel) {
        cartProducts = cartProducts.updateItemOrdered(cartProductModel.toDomain(), true)
        updateOrder()
    }

    private fun CartProductInfoList.toPresentationList(): List<CartProductInfoModel> {
        return this.items.map {
            it.toPresentation()
        }
    }

    override fun deleteProductInOrder(cartProductModel: CartProductInfoModel) {
        cartProducts = cartProducts.updateItemOrdered(cartProductModel.toDomain(), false)
        updateOrder()
    }

    override fun updateProductCount(cartProductModel: CartProductInfoModel, count: Int) {
        cartRepository.updateCartItemQuantity(
            cartProductModel.id,
            count,
        ) {
            cartProducts = cartProducts.updateItemCount(cartProductModel.toDomain(), count)
            refreshCurrentPageItems()
            if (cartProductModel.isOrdered) updateOrder()
        }
    }
    override fun changeCurrentPageProductsOrder(allIsOrdered: Boolean) {
        if (allIsOrdered) {
            cartProducts = cartProducts.replaceItemList(pageProducts.updateAllItemOrdered(true))
        } else if (!allIsOrdered && pageProducts.isAllOrdered) {
            cartProducts = cartProducts.replaceItemList(pageProducts.updateAllItemOrdered(false))
        }
        refreshCurrentPageItems()
    }

    override fun checkCurrentPageProductsIsOrdered() {
        if (pageProducts.isAllOrdered) {
            view.setAllIsOrderCheck(true)
        } else {
            view.setAllIsOrderCheck(false)
        }
    }

    override fun updateOrderPrice() {
        val price = cartProducts.orders.totalPrice
        view.setOrderPrice(price)
    }

    override fun updateOrderCount() {
        val count = cartProducts.orders.count
        view.setOrderCount(count)
    }

    override fun deleteProductItem(
        cartProductModel: CartProductInfoModel,
    ) {
        cartRepository.deleteCartItem(cartProductModel.id) {
            cartProducts = cartProducts.delete(cartProductModel.toDomain())
            updateAllState()
        }
    }

    override fun plusPage() {
        paging.plusPage()
        view.setPage(paging.currentPage.value.toString())
    }

    override fun minusPage() {
        paging.minusPage()
        view.setPage(paging.currentPage.value.toString())
    }

    override fun refreshCurrentPageItems() {
        val items = pageProducts.toPresentationList()
        view.setCartItems(items)
    }

    override fun order() {
        val orderProductModel = OrderProductsModel(
            cartProducts
                .orders
                .items
                .map { OrderProductModel(it.product.toPresentation(), it.count) }
        )
        view.showOrderView(orderProductModel)
    }

    companion object {
        private const val DEFAULT_INIT_PAGE = 1
    }
}
