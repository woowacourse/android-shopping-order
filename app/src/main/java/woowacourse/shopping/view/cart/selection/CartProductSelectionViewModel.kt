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

class CartProductSelectionViewModel(
    private val repository: CartProductRepository,
) : ViewModel(),
    CartProductSelectionEventHandler {
    private val selectedProducts = MutableLiveData<Set<CartProduct>>(emptySet())
    val selectedIds get() = selectedProducts.value.orEmpty().map { it.id }.toSet()

    private val _products = MutableLiveData<List<CartProductItem>>()
    val products: LiveData<List<CartProductItem>> get() = _products

    val totalPrice: LiveData<Int> =
        selectedProducts.map { products ->
            products.sumOf { it.totalPrice }
        }

    val totalCount: LiveData<Int> =
        selectedProducts.map { products ->
            products.sumOf { it.quantity }
        }

    val isSelectedAll: LiveData<Boolean> =
        _products.map { products ->
            products.all { it.isSelected } && products.isNotEmpty()
        }

    val canOrder: LiveData<Boolean> =
        selectedProducts.map { products ->
            products.isNotEmpty()
        }

    private val _page = MutableLiveData(FIRST_PAGE_NUMBER)
    val page: LiveData<Int> get() = _page

    private val _hasNext = MutableLiveData(false)
    val hasNext: LiveData<Boolean> get() = _hasNext

    private val _hasPrevious = MutableLiveData(false)
    val hasPrevious: LiveData<Boolean> get() = _hasPrevious

    private val _isSinglePage = MutableLiveData(true)
    val isSinglePage: LiveData<Boolean> get() = _isSinglePage

    private val _isFinishedLoading = MutableLiveData(false)
    val isFinishedLoading: LiveData<Boolean> get() = _isFinishedLoading

    override fun loadNextProducts() {
        val nextPage = page.value?.plus(1) ?: FIRST_PAGE_NUMBER
        if (hasNext.value == true) loadPage(nextPage)
    }

    override fun loadPreviousProducts() {
        val prevPage = page.value?.minus(1) ?: FIRST_PAGE_NUMBER
        if (_hasPrevious.value == true) loadPage(prevPage)
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
                    val currentSelected = selectedProducts.value.orEmpty()
                    if (item in currentSelected) {
                        selectedProducts.value = currentSelected - item
                    }
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onQuantityIncreaseClick(item: CartProduct) {
        val cartProductItem =
            products.value.orEmpty().firstOrNull { it.cartProduct.product.id == item.product.id }
                ?: return

        viewModelScope.launch {
            val result =
                repository.updateQuantity(
                    cartProductItem.cartProduct,
                    cartProductItem.cartProduct.quantity + QUANTITY_TO_ADD,
                )

            result
                .onSuccess {
                    loadPage(_page.value ?: FIRST_PAGE_NUMBER)
                    val currentSelected = selectedProducts.value.orEmpty()
                    if (item in currentSelected) {
                        val newItem =
                            item.copy(quantity = cartProductItem.cartProduct.quantity + QUANTITY_TO_ADD)
                        selectedProducts.value = currentSelected - item + newItem
                    }
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onQuantityDecreaseClick(item: CartProduct) {
        val cartProductItem =
            products.value.orEmpty()
                .firstOrNull { it.cartProduct.product.id == item.product.id }
                ?: return
        if (cartProductItem.cartProduct.quantity == 1) return

        viewModelScope.launch {
            val result =
                repository.updateQuantity(
                    cartProductItem.cartProduct,
                    cartProductItem.cartProduct.quantity - QUANTITY_TO_ADD,
                )

            result
                .onSuccess {
                    loadPage(_page.value ?: FIRST_PAGE_NUMBER)
                    val currentSelected = selectedProducts.value.orEmpty()
                    if (item in currentSelected) {
                        val newItem =
                            item.copy(quantity = cartProductItem.cartProduct.quantity - QUANTITY_TO_ADD)
                        selectedProducts.value = currentSelected - item + newItem
                    }
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onSelectItem(item: CartProduct) {
        val isSelected = selectedProducts.value?.contains(item) == true
        val currentSelected = selectedProducts.value.orEmpty()
        if (currentSelected.contains(item)) {
            selectedProducts.value = currentSelected - item
        } else {
            selectedProducts.value = currentSelected + item
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
        val currentSelected = selectedProducts.value.orEmpty()
        val isAllSelected = isSelectedAll.value == true
        if (isAllSelected == true) {
            selectedProducts.value =
                currentSelected - currentProducts.map { it.cartProduct }.toSet()
        } else {
            selectedProducts.value =
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
                                CartProductItem(it, it in selectedProducts.value.orEmpty())
                            }
                        )
                    val hasNext = pagedResult.hasNext
                    updatePageState(page, hasNext)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    private fun updatePageState(
        page: Int,
        hasNext: Boolean,
    ) {
        _page.value = page
        val hasPrevious = page > FIRST_PAGE_NUMBER
        _hasPrevious.value = page > FIRST_PAGE_NUMBER
        _hasNext.value = hasNext
        _isSinglePage.value = !hasNext && !hasPrevious
    }

    companion object {
        private const val QUANTITY_TO_ADD = 1
        private const val FIRST_PAGE_NUMBER = 1
        private const val PAGE_SIZE = 5
    }
}
