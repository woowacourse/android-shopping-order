package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.presentation.cart.event.CartEventHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.util.SingleLiveEvent

class CartViewModel(
    private val cartRepository: CartItemRepository,
) : ViewModel(),
    CartEventHandler {
    private val _isNextButtonEnabled = MutableLiveData(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    private val _isPrevButtonEnabled = MutableLiveData(false)
    val isPrevButtonEnabled: LiveData<Boolean> = _isPrevButtonEnabled

    private val _pageEvent = SingleLiveEvent<Int>()
    val pageEvent: LiveData<Int> = _pageEvent

    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private var currentPage: Int = INITIAL_PAGE

    private val _pagingData = MutableLiveData<PagingData>()
    val pagingData: LiveData<PagingData> = _pagingData

    private val _totalOrderPrice = MutableLiveData<Int>(0)
    val totalOrderPrice: LiveData<Int> = _totalOrderPrice

    private val _checkedProductCount = MutableLiveData<Int>(0)
    val checkedProductCount: LiveData<Int> = _checkedProductCount

    private val _checkedProducts = MutableLiveData<List<ProductUiModel>>()
    val checkProducts: LiveData<List<ProductUiModel>> = _checkedProducts

    val isAllChecked = MutableLiveData(false)

    init {
        loadCartProducts()
    }

    override fun onDeleteProduct(cartProduct: ProductUiModel) {
        cartRepository.deleteCartItem(cartProduct.id) { result ->
            result
                .onSuccess {
                    loadCartProducts()
                    setOrderData()
                }
        }
    }

    override fun onNextPage() {
        val hasNext = pagingData.value?.hasNext == true
        if (currentPage >= 0 && hasNext) {
            currentPage++
            _pageEvent.postValue(currentPage)
            loadCartProducts()
            isAllChecked.value = false
        }
    }

    override fun onPrevPage() {
        if (currentPage > 0) {
            currentPage--
            _pageEvent.postValue(currentPage)
            loadCartProducts()
            isAllChecked.value = false
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        val newProduct = product.copy(quantity = product.quantity + 1)
        cartRepository.updateCartItemQuantity(newProduct.id, newProduct.quantity) { result ->
            result
                .onSuccess {
                    _product.postValue(newProduct)
                    updateProductInPagingData(newProduct)
                    setOrderData()
                }
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val newQuantity = if (product.quantity > 1) product.quantity - 1 else 1
        val newProduct = product.copy(quantity = newQuantity)
        cartRepository.updateCartItemQuantity(newProduct.id, newProduct.quantity) { result ->
            result
                .onSuccess {
                    _product.postValue(newProduct)
                    updateProductInPagingData(newProduct)
                    setOrderData()
                }
        }
    }

    override fun isNextButtonEnabled(): Boolean = _pagingData.value?.hasNext == true

    override fun isPrevButtonEnabled(): Boolean = _pagingData.value?.hasPrevious == true

    override fun isPaginationEnabled(): Boolean =
        (_isNextButtonEnabled.value == true) || (_isPrevButtonEnabled.value == true)

    override fun getPage(): Int = currentPage

    override fun onCheckProduct(product: ProductUiModel) {
        val currentPagingData = _pagingData.value ?: return

        val updateProducts = currentPagingData.products.map {
            if (it.id == product.id) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it
            }
        }

        _pagingData.value = currentPagingData.copy(products = updateProducts)
        setOrderData()
    }

    fun onCheckAllCartItems() {
        pagingData.value?.let { currentPagingData ->
            val updatedProducts = currentPagingData.products.map {
                it.copy(isChecked = isAllChecked.value!!)
            }
            _pagingData.value = currentPagingData.copy(products = updatedProducts)
            setOrderData()
        }
    }

    private fun setOrderData() {
        val currentProducts = _pagingData.value?.products ?: emptyList()
        val previousCheckedProducts = _checkedProducts.value ?: emptyList()

        val currentCheckedProducts = currentProducts.filter { it.isChecked }
        val currentUncheckedIds = currentProducts.filterNot { it.isChecked }.map { it.id }

        val remainedCheckedProducts =
            previousCheckedProducts.filterNot { it.id in currentUncheckedIds }

        val newCheckedProducts =
            (remainedCheckedProducts + currentCheckedProducts).distinctBy { it.id }

        _checkedProducts.postValue(newCheckedProducts)
        _totalOrderPrice.postValue(newCheckedProducts.sumOf { it.quantity * it.price })
        _checkedProductCount.postValue(newCheckedProducts.count())
    }

    private fun loadCartProducts(pageSize: Int = PAGE_SIZE) {
        cartRepository.getCartItems(currentPage, pageSize) { result ->
            result.onSuccess { pagingData ->
                if (pagingData.products.isEmpty() && currentPage > 0) {
                    currentPage--
                    _pageEvent.postValue(currentPage)
                    loadCartProducts()
                } else {
                    _pagingData.postValue(pagingData)
                }
            }
        }
    }

    private fun updateProductInPagingData(updatedProduct: ProductUiModel) {
        _pagingData.value = _pagingData.value?.let { paging ->
            val updatedList = paging.products.map {
                if (it.id == updatedProduct.id) updatedProduct else it
            }
            paging.copy(products = updatedList)
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val INITIAL_PAGE = 0

        val FACTORY: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CartViewModel(
                        RepositoryProvider.cartItemRepository,
                    )
                }
            }
    }
}
