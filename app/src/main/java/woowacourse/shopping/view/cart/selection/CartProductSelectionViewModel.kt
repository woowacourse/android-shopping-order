package woowacourse.shopping.view.cart.selection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.view.cart.selection.adapter.CartProductItem
import woowacourse.shopping.view.util.Error
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class CartProductSelectionViewModel(
    private val repository: CartProductRepository,
) : ViewModel(),
    CartProductSelectionEventHandler {
    private val _selectedProducts = MutableLiveData<Set<CartProduct>>(emptySet())
    val selectedProducts get() = _selectedProducts.value.orEmpty()

    private val _products = MutableLiveData<List<CartProductItem>>()
    val products: LiveData<List<CartProductItem>> get() = _products

    private val _page = MutableLiveData(FIRST_PAGE_NUMBER)
    val page: LiveData<Int> get() = _page

    private val _hasNext = MutableLiveData(false)
    val hasNext: LiveData<Boolean> get() = _hasNext

    private val _isFinishedLoading = MutableLiveData(false)
    val isFinishedLoading: LiveData<Boolean> get() = _isFinishedLoading

    private val _errorEvent = MutableSingleLiveData<Error>()
    val errorEvent: SingleLiveData<Error> get() = _errorEvent

    val totalPrice: LiveData<Int> =
        _selectedProducts.map { products ->
            products.sumOf { it.totalPrice }
        }

    val totalCount: LiveData<Int> =
        _selectedProducts.map { products ->
            products.sumOf { it.quantity }
        }

    val isSelectedAll: LiveData<Boolean> =
        _products.map { products ->
            products.all { it.isSelected } && products.isNotEmpty()
        }

    val canOrder: LiveData<Boolean> =
        _selectedProducts.map { products ->
            products.isNotEmpty()
        }

    val hasPrevious: LiveData<Boolean> =
        _page.map { page ->
            page > FIRST_PAGE_NUMBER
        }

    val isSinglePage: LiveData<Boolean> =
        _hasNext.map { hasNext ->
            hasNext == false && hasPrevious.value == false
        }

    override fun loadNextProducts() {
        val nextPage = page.value?.plus(1) ?: FIRST_PAGE_NUMBER
        if (hasNext.value == true) loadPage(nextPage)
    }

    override fun loadPreviousProducts() {
        val prevPage = page.value?.minus(1) ?: FIRST_PAGE_NUMBER
        if (hasPrevious.value == true) loadPage(prevPage)
    }

    override fun onProductRemoveClick(item: CartProduct) {
        viewModelScope.launch {
            val result = repository.delete(item.id)

            result
                .onSuccess {
                    val currentPage = page.value ?: FIRST_PAGE_NUMBER

                    if (products.value?.size == 1 && currentPage > FIRST_PAGE_NUMBER) {
                        loadPage(currentPage - 1)
                    } else {
                        loadPage(currentPage)
                    }
                    val currentSelected = _selectedProducts.value.orEmpty()
                    if (item in currentSelected) {
                        _selectedProducts.value = currentSelected - item
                    }
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToDelete)
                }
        }
    }

    override fun onQuantityIncreaseClick(id: Int) {
        val cartProductItem =
            products.value.orEmpty().firstOrNull { it.cartProduct.id == id }
                ?: return

        viewModelScope.launch {
            val newQuantity = cartProductItem.cartProduct.quantity + QUANTITY_STEP
            val result =
                repository.updateQuantity(
                    cartProductItem.cartProduct,
                    newQuantity,
                )

            result
                .onSuccess {
                    loadPage(_page.value ?: FIRST_PAGE_NUMBER)
                    updateSelectedProducts(cartProductItem.cartProduct, newQuantity)
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToIncrease)
                }
        }
    }

    override fun onQuantityDecreaseClick(id: Int) {
        val cartProductItem =
            products.value.orEmpty()
                .firstOrNull { it.cartProduct.id == id }
                ?: return
        if (cartProductItem.cartProduct.quantity == 1) return

        viewModelScope.launch {
            val newQuantity = cartProductItem.cartProduct.quantity - QUANTITY_STEP
            val result =
                repository.updateQuantity(
                    cartProductItem.cartProduct,
                    newQuantity,
                )

            result
                .onSuccess {
                    loadPage(_page.value ?: FIRST_PAGE_NUMBER)
                    updateSelectedProducts(cartProductItem.cartProduct, newQuantity)
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToDecrease)
                }
        }
    }

    private fun updateSelectedProducts(
        item: CartProduct,
        newQuantity: Int,
    ) {
        val currentSelected = _selectedProducts.value.orEmpty()
        if (item in currentSelected) {
            val newItem =
                item.copy(quantity = newQuantity)
            _selectedProducts.value = currentSelected - item + newItem
        }
    }

    override fun onSelectItem(item: CartProduct) {
        val currentSelected = _selectedProducts.value.orEmpty()
        val isSelected = item in currentSelected

        _selectedProducts.value =
            if (isSelected) {
                currentSelected - item
            } else {
                currentSelected + item
            }
        _products.value =
            products.value.orEmpty().map {
                if (it.cartProduct.id == item.id) {
                    it.copy(isSelected = !isSelected)
                } else {
                    it
                }
            }
    }

    override fun onSelectAllItems() {
        val currentProducts = _products.value.orEmpty()
        val currentSelected = _selectedProducts.value.orEmpty()
        val isAllSelected = isSelectedAll.value == true
        if (isAllSelected == true) {
            _selectedProducts.value =
                currentSelected - currentProducts.map { it.cartProduct }.toSet()
        } else {
            _selectedProducts.value =
                currentSelected + currentProducts.map { it.cartProduct }.toSet()
        }
        val updatedProducts = currentProducts.map { it.copy(isSelected = !isAllSelected) }
        _products.value = updatedProducts
    }

    fun loadPage(page: Int = FIRST_PAGE_NUMBER) {
        _isFinishedLoading.value = false
        viewModelScope.launch {
            val result = repository.getPagedProducts(page - 1, PAGE_SIZE)

            result
                .onSuccess { pagedResult ->
                    _isFinishedLoading.value = true
                    _products.value =
                        (
                            pagedResult.items.map {
                                CartProductItem(it, it in _selectedProducts.value.orEmpty())
                            }
                        )
                    val hasNext = pagedResult.hasNext
                    updatePageState(page, hasNext)
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToLoadProduct)
                }
        }
    }

    private fun updatePageState(
        page: Int,
        hasNext: Boolean,
    ) {
        _page.value = page
        _hasNext.value = hasNext
    }

    companion object {
        private const val QUANTITY_STEP = 1
        private const val FIRST_PAGE_NUMBER = 1
        private const val PAGE_SIZE = 5
    }
}
