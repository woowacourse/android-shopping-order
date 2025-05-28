package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.view.cart.adapter.CartProductItem

class ShoppingCartViewModel(
    private val repository: CartProductRepository,
) : ViewModel(),
    ShoppingCartEventHandler {
    private val selectedId: MutableSet<Int> = mutableSetOf()

    private val _products = MutableLiveData<List<CartProductItem>>()
    val products: LiveData<List<CartProductItem>> get() = _products

    private val _totalPrice = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalCount = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    private var _page = MutableLiveData(FIRST_PAGE_NUMBER)
    val page: LiveData<Int> get() = _page

    private val _hasNext = MutableLiveData(false)
    val hasNext: LiveData<Boolean> get() = _hasNext

    private val _hasPrevious = MutableLiveData(false)
    val hasPrevious: LiveData<Boolean> get() = _hasPrevious

    private val _isSinglePage = MutableLiveData(true)
    val isSinglePage: LiveData<Boolean> get() = _isSinglePage

    private val _isSelectedAll = MutableLiveData(false)
    val isSelectedAll: LiveData<Boolean> get() = _isSelectedAll

    private val _isFinishedLoading = MutableLiveData(false)
    val isFinishedLoading: LiveData<Boolean> get() = _isFinishedLoading

    init {
        loadPage(FIRST_PAGE_NUMBER)
    }

    override fun loadNextProducts() {
        val nextPage = page.value?.plus(1) ?: FIRST_PAGE_NUMBER
        if (hasNext.value == true) loadPage(nextPage)
    }

    override fun loadPreviousProducts() {
        val prevPage = page.value?.minus(1) ?: FIRST_PAGE_NUMBER
        if (_hasPrevious.value == true) loadPage(prevPage)
    }

    override fun onProductRemoveClick(item: CartProduct) {
        repository.delete(item.id) {
            val currentPage = page.value ?: FIRST_PAGE_NUMBER

            if (products.value?.size == 1 && currentPage > FIRST_PAGE_NUMBER) {
                loadPage(currentPage - 1)
            } else {
                loadPage(currentPage)
            }
        }

        if (item.id in selectedId) {
            _totalCount.value = totalCount.value?.minus(item.quantity)
            _totalPrice.value = totalPrice.value?.minus(item.totalPrice)
        }
    }

    override fun onQuantityIncreaseClick(item: CartProduct) {
        val cartProductItem =
            products.value.orEmpty().first { it.cartProduct.product.id == item.product.id }
        repository.updateQuantity(cartProductItem.cartProduct, 1) {
            loadPage(_page.value ?: FIRST_PAGE_NUMBER)
        }
        if (item.id in selectedId) {
            _totalCount.value = totalCount.value?.plus(1)
            _totalPrice.value = totalPrice.value?.plus(item.product.price)
        }
    }

    override fun onQuantityDecreaseClick(item: CartProduct) {
        val cartProductItem =
            products.value.orEmpty().first { it.cartProduct.product.id == item.product.id }
        if (cartProductItem.cartProduct.quantity == 1) return
        repository.updateQuantity(cartProductItem.cartProduct, -1) {
            loadPage(_page.value ?: FIRST_PAGE_NUMBER)
        }
        if (item.id in selectedId) {
            _totalCount.value = totalCount.value?.minus(1)
            _totalPrice.value = totalPrice.value?.minus(item.product.price)
        }
    }

    override fun onSelectItem(item: CartProduct) {
        val isSelected = item.id in selectedId
        if (isSelected) {
            selectedId.remove(item.id)
            _totalCount.value = totalCount.value?.minus(item.quantity)
            _totalPrice.value = totalPrice.value?.minus(item.totalPrice)
        } else {
            selectedId.add(item.id)
            _totalCount.value = totalCount.value?.plus(item.quantity)
            _totalPrice.value = totalPrice.value?.plus(item.totalPrice)
        }
        _products.value =
            products.value.orEmpty().map {
                if (it.cartProduct.id == item.id) {
                    it.copy(isSelected = !isSelected)
                } else {
                    it
                }
            }
        updateIsSelectedAll()
    }

    override fun onSelectAllItems() {
        val currentProducts = products.value.orEmpty()
        val allIds = currentProducts.map { it.cartProduct.id }
        val isSelectedAll = isSelectedAll.value ?: false

        if (isSelectedAll) {
            selectedId.removeAll(allIds.toSet())
            currentProducts.forEach {
                if (it.isSelected) {
                    _totalCount.value = totalCount.value?.minus(it.cartProduct.quantity)
                    _totalPrice.value = totalPrice.value?.minus(it.cartProduct.totalPrice)
                }
            }
        } else {
            selectedId.addAll(allIds)
            currentProducts.forEach {
                if (!it.isSelected) {
                    _totalCount.value = totalCount.value?.plus(it.cartProduct.quantity)
                    _totalPrice.value = totalPrice.value?.plus(it.cartProduct.totalPrice)
                }
            }
        }
        val updatedProducts = currentProducts.map { it.copy(isSelected = !isSelectedAll) }
        _isSelectedAll.value = !isSelectedAll
        _products.value = updatedProducts
    }

    private fun loadPage(page: Int) {
        _isFinishedLoading.value = false
        repository.getPagedProducts(page - 1, PAGE_SIZE) { result ->
            _isFinishedLoading.value = true
            _products.value = (result.items.map { CartProductItem(it, it.id in selectedId) })
            val hasNext = result.hasNext
            updatePageState(page, hasNext)
            updateIsSelectedAll()
        }
    }

    private fun updateIsSelectedAll() {
        val isAllSelected = products.value?.all { it.isSelected } ?: false
        _isSelectedAll.postValue(isAllSelected)
    }

    private fun updatePageState(
        page: Int,
        hasNext: Boolean,
    ) {
        _page.postValue(page)
        val hasPrevious = page > FIRST_PAGE_NUMBER
        _hasPrevious.postValue(page > FIRST_PAGE_NUMBER)
        _hasNext.postValue(hasNext)
        _isSinglePage.postValue(!hasNext && !hasPrevious)
    }

    companion object {
        private const val FIRST_PAGE_NUMBER = 1
        private const val PAGE_SIZE = 5
    }
}
