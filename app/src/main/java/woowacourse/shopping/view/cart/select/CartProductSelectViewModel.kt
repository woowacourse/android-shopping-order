package woowacourse.shopping.view.cart.select

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.view.cart.select.adapter.CartProductItem

class CartProductSelectViewModel(
    private val repository: CartProductRepository,
) : ViewModel(),
    CartProductSelectEventHandler {
    private val _cartProductItems = MutableLiveData<List<CartProductItem>>()
    val cartProductItems: LiveData<List<CartProductItem>> get() = _cartProductItems

    private var _page = MutableLiveData(FIRST_PAGE_NUMBER)
    val page: LiveData<Int> get() = _page

    private val _hasNext = MutableLiveData(false)
    val hasNext: LiveData<Boolean> get() = _hasNext

    private val _hasPrevious = MutableLiveData(false)
    val hasPrevious: LiveData<Boolean> get() = _hasPrevious

    private val _isSinglePage = MutableLiveData(true)
    val isSinglePage: LiveData<Boolean> get() = _isSinglePage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _selectedCartProducts = MutableLiveData<Set<CartProduct>>(emptySet())
    val selectedCartProducts: LiveData<Set<CartProduct>> get() = _selectedCartProducts

    val totalPrice: LiveData<Int> =
        _selectedCartProducts.map { products ->
            products.sumOf { it.totalPrice }
        }

    val totalCount: LiveData<Int> =
        _selectedCartProducts.map { products ->
            products.sumOf { it.quantity }
        }

    val isSelectedAll: LiveData<Boolean> =
        _cartProductItems.map { products ->
            products.isNotEmpty() && products.all { it.isSelected }
        }

    fun loadPage(page: Int = FIRST_PAGE_NUMBER) {
        viewModelScope.launch {
            _isLoading.value = true

            repository
                .getPagedProducts(page - 1, PAGE_SIZE)
                .onSuccess { pagedResult ->
                    _isLoading.value = false
                    _cartProductItems.value =
                        pagedResult.items.map {
                            CartProductItem(it, it in selectedCartProducts.value.orEmpty())
                        }
                    updatePageState(page, pagedResult.hasNext)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onNextPageClick() {
        val nextPage = page.value?.plus(1) ?: FIRST_PAGE_NUMBER
        if (hasNext.value == true) loadPage(nextPage)
    }

    override fun onPreviousPageClick() {
        val previousPage = page.value?.minus(1) ?: FIRST_PAGE_NUMBER
        if (hasPrevious.value == true) loadPage(previousPage)
    }

    override fun onProductRemoveClick(item: CartProduct) {
        viewModelScope.launch {
            repository
                .delete(item.id)
                .onSuccess {
                    val currentPage = page.value ?: FIRST_PAGE_NUMBER
                    if (cartProductItems.value?.size == 1 && currentPage > FIRST_PAGE_NUMBER) {
                        loadPage(currentPage - 1)
                    } else {
                        loadPage(currentPage)
                    }

                    val currentSelected = selectedCartProducts.value.orEmpty()
                    if (item in currentSelected) {
                        _selectedCartProducts.postValue(currentSelected - item)
                    }
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onQuantityIncreaseClick(item: CartProduct) {
        updateCartProduct(item, QUANTITY_TO_ADD)
    }

    override fun onQuantityDecreaseClick(item: CartProduct) {
        updateCartProduct(item, -QUANTITY_TO_ADD)
    }

    override fun onSelectItem(item: CartProduct) {
        val currentSelected = selectedCartProducts.value.orEmpty()
        val isSelected = item in currentSelected
        if (isSelected) {
            _selectedCartProducts.postValue(currentSelected - item)
        } else {
            _selectedCartProducts.postValue(currentSelected + item)
        }
        _cartProductItems.value =
            cartProductItems.value.orEmpty().map {
                if (it.cartProduct.id == item.id) {
                    it.copy(isSelected = !isSelected)
                } else {
                    it
                }
            }
    }

    override fun onSelectAllClick() {
        val currentProducts = cartProductItems.value.orEmpty()
        val currentSelected = selectedCartProducts.value.orEmpty()
        val isSelectedAll = isSelectedAll.value ?: false
        if (isSelectedAll) {
            _selectedCartProducts.postValue(
                currentSelected - currentProducts.map { it.cartProduct }.toSet(),
            )
        } else {
            _selectedCartProducts.postValue(
                currentSelected + currentProducts.map { it.cartProduct }.toSet(),
            )
        }
        val updatedProducts = currentProducts.map { it.copy(isSelected = !isSelectedAll) }
        _cartProductItems.value = updatedProducts
    }

    private fun updateCartProduct(
        item: CartProduct,
        quantityDelta: Int,
    ) {
        viewModelScope.launch {
            val existing =
                cartProductItems.value.orEmpty().firstOrNull { it.cartProduct.id == item.id }
                    ?: return@launch
            val newQuantity = existing.cartProduct.quantity + quantityDelta
            if (newQuantity < MINIMUM_QUANTITY) return@launch

            repository
                .updateQuantity(existing.cartProduct, quantityDelta)
                .onSuccess {
                    loadPage(page.value ?: FIRST_PAGE_NUMBER)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
            val currentSelected = selectedCartProducts.value.orEmpty()
            if (item in currentSelected) {
                val newItem = item.copy(quantity = newQuantity)
                _selectedCartProducts.postValue(currentSelected - item + newItem)
            }
        }
    }

    private fun updatePageState(
        page: Int,
        hasNext: Boolean,
    ) {
        _page.postValue(page)
        val hasPrevious = page > FIRST_PAGE_NUMBER
        _hasPrevious.postValue(hasPrevious)
        _hasNext.postValue(hasNext)
        _isSinglePage.postValue(!hasNext && !hasPrevious)
    }

    companion object {
        private const val FIRST_PAGE_NUMBER = 1
        private const val PAGE_SIZE = 5
        private const val QUANTITY_TO_ADD = 1
        private const val MINIMUM_QUANTITY = 1
    }
}
