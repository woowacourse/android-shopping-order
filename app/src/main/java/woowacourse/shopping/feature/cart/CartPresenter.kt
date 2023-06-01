package woowacourse.shopping.feature.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.CartProducts
import com.example.domain.repository.CartRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.PageUiModel

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
) : CartContract.Presenter {

    override lateinit var page: PageUiModel
        private set

    private var _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private var _totalCount: MutableLiveData<Int> = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    private var _allSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    val allSelected: LiveData<Boolean> get() = _allSelected

    private lateinit var cartProducts: CartProducts

    override fun setup() {
        cartProducts = cartRepository.getAll()
        page = PageUiModel(cartProducts.size, 1)
        _allSelected.value = isAllSelected()

        updateTotalSelectedValues()
        changePageState(getCurrentPageItems())
    }

    override fun deleteCartProduct(cartProduct: CartProductUiModel) {
        cartRepository.deleteProduct(cartProduct.cartProductId.toInt())
        cartProducts.delete(cartProduct.toDomain())

        this.page = this.page.copy(allSize = this.page.allSize - 1)

        var loadedItems = getCurrentPageItems()
        if (loadedItems.isEmpty() && this.page.currentPage != 1) {
            this.page = this.page.previousPage()
            loadedItems = getCurrentPageItems()
        }

        changePageState(loadedItems)
        updateTotalSelectedValues()
        _allSelected.value = isAllSelected()
    }

    override fun loadPreviousPage() {
        this.page = this.page.previousPage()

        changePageState(getCurrentPageItems())
    }

    override fun loadNextPage() {
        this.page = this.page.nextPage()

        changePageState(getCurrentPageItems())
    }

    override fun setPage(page: PageUiModel) {
        this.page = page
        changePageState(getCurrentPageItems())
    }

    override fun increaseCartProduct(cartProduct: CartProductUiModel, previousCount: Int) {
        if (previousCount == 0) {
            cartRepository.addProduct(cartProduct.productUiModel.toDomain())
            cartProducts = cartRepository.getAll()
        } else {
            cartRepository.updateProduct(cartProduct.cartProductId.toInt(), previousCount + 1)
            cartProducts.updateProductCount(cartProduct.toDomain(), previousCount + 1)
        }
        updateTotalSelectedValues()
    }

    override fun decreaseCartProduct(cartProduct: CartProductUiModel, previousCount: Int) {
        if (previousCount == 1) {
            deleteCartProduct(cartProduct)
        } else {
            cartRepository.updateProduct(cartProduct.cartProductId.toInt(), previousCount - 1)
            cartProducts.updateProductCount(cartProduct.toDomain(), previousCount - 1)
        }
        updateTotalSelectedValues()
    }

    override fun toggleCartProduct(cartProduct: CartProductUiModel, isSelected: Boolean) {
        cartProducts.updateSelection(cartProduct.toDomain(), isSelected)

        if (!isSelected) _allSelected.value = false
        else _allSelected.value = isAllSelected()
        updateTotalSelectedValues()
    }

    override fun toggleAllProductOnPage(isSelected: Boolean) {
        val notSelectedItems = getCurrentPageItems().filter { !it.isSelected }
        if (isSelected) {
            selectAll(notSelectedItems)
            return
        }
        if (notSelectedItems.isEmpty()) {
            deselectAll()
        }
    }

    override fun moveToPayment() {
        view.showPaymentScreen(
            cartProducts.selectedProducts().map { it.toPresentation() },
            totalPrice.value ?: 0
        )
    }

    private fun selectAll(notSelectedItems: List<CartProductUiModel>) {
        notSelectedItems.forEach { cartProduct ->
            cartProducts.updateSelection(cartProduct.toDomain(), true)
        }
        updateTotalSelectedValues()
        changePageState(getCurrentPageItems())
    }

    private fun deselectAll() {
        getCurrentPageItems().forEach { cartProduct ->
            cartProducts.updateSelection(cartProduct.toDomain(), false)
        }
        updateTotalSelectedValues()
        changePageState(getCurrentPageItems())
    }

    private fun updateTotalSelectedValues() {
        val selectedItems = cartProducts.selectedProducts().map { it.toPresentation() }

        _totalPrice.value = selectedItems.sumOf { it.totalPrice() }
        _totalCount.value = selectedItems.size
    }

    private fun isAllSelected(): Boolean = getCurrentPageItems().all { it.isSelected }

    private fun getCurrentPageItems(): List<CartProductUiModel> {
        return cartProducts.cartProductsByRange(page.currentPageStart, page.currentPageEnd)
            .map { it.toPresentation() }
    }

    private fun changePageState(itemModels: List<CartProductUiModel>) {
        _allSelected.value = isAllSelected()
        view.changeCartProducts(itemModels)
        view.setPageState(
            this.page.hasPreviousPage(),
            this.page.hasNextPage(),
            this.page.currentPage
        )
    }
}
