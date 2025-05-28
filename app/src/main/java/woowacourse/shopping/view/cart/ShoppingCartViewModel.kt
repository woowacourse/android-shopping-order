package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class ShoppingCartViewModel(
    private val repository: CartProductRepository,
) : ViewModel(),
    ShoppingCartEventHandler {
    private val _products = MutableLiveData<List<CartProduct>>()
    val products: LiveData<List<CartProduct>> = _products

    private var _page = MutableLiveData(FIRST_PAGE_NUMBER)
    val page: LiveData<Int> = _page

    private val _hasNext = MutableLiveData(false)
    val hasNext: LiveData<Boolean> = _hasNext

    private val _hasPrevious = MutableLiveData(false)
    val hasPrevious: LiveData<Boolean> = _hasPrevious

    private val _isSinglePage = MutableLiveData(true)
    val isSinglePage: LiveData<Boolean> = _isSinglePage

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
    }

    override fun onQuantityIncreaseClick(item: CartProduct) {
        repository.updateQuantity(item.product.id, 1) {
            loadPage(_page.value ?: FIRST_PAGE_NUMBER)
        }
    }

    override fun onQuantityDecreaseClick(item: CartProduct) {
        val quantity = products.value?.firstOrNull { it.product.id == item.product.id }?.quantity
        if (quantity == 1) return
        repository.updateQuantity(item.product.id, -1) {
            loadPage(_page.value ?: FIRST_PAGE_NUMBER)
        }
    }

    private fun loadPage(page: Int) {
        repository.getPagedProducts(page - 1, PAGE_SIZE) { result ->
            _products.postValue(result.items)
            val hasNext = result.hasNext
            updatePageState(page, hasNext)
        }
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
