package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.presentation.cart.event.CartEventHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CartViewModel(
    private val cartRepository: CartItemRepository,
) : ViewModel(),
    CartEventHandler {

    private val _isNextButtonEnabled = MutableLiveData(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    private val _isPrevButtonEnabled = MutableLiveData(false)
    val isPrevButtonEnabled: LiveData<Boolean> = _isPrevButtonEnabled

    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _pagingData = MutableLiveData<PagingData>()
    val pagingData: LiveData<PagingData> = _pagingData

    private val _checkedProducts = MutableLiveData<List<ProductUiModel>>(emptyList())
    val checkedProducts: LiveData<List<ProductUiModel>> = _checkedProducts

    val totalOrderPrice: LiveData<Int> = checkedProducts.map { checkedProducts ->
        checkedProducts.sumOf { it.quantity * it.price }
    }

    val checkedProductCount: LiveData<Int> = checkedProducts.map { checkedProducts ->
        checkedProducts.count()
    }

    val isAllChecked = MutableLiveData(false)

    init {
        loadCartProducts()
    }

    override fun onDeleteProduct(cartProduct: ProductUiModel) {
        cartRepository.deleteCartItem(cartProduct.id) { result ->
            result.onSuccess {
                val currentPage = pagingData.value?.page ?: INITIAL_PAGE
                loadCartProducts(currentPage)
                setCheckedProducts(cartProduct)
            }
        }
    }

    private fun setCheckedProducts(cartProduct: ProductUiModel) {
        _checkedProducts.value = _checkedProducts.value?.filterNot { it.id == cartProduct.id }
    }

    override fun onNextPage() {
        val currentPage = _pagingData.value?.page ?: INITIAL_PAGE
        if (_pagingData.value?.hasNext == true) {
            loadCartProducts(page = currentPage + 1)
            isAllChecked.value = false
        }
    }

    override fun onPrevPage() {
        val currentPage = _pagingData.value?.page ?: INITIAL_PAGE
        if (currentPage > 0) {
            loadCartProducts(page = currentPage - 1)
            isAllChecked.value = false
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        val newProduct = product.copy(quantity = product.quantity + 1)
        cartRepository.updateCartItemQuantity(newProduct.id, newProduct.quantity) { result ->
            result.onSuccess {
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
            result.onSuccess {
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

    override fun getPage(): Int = pagingData.value?.page ?: 0

    override fun onCheckProduct(product: ProductUiModel) {
        val currentPagingData = _pagingData.value ?: return

        val updatedProducts = currentPagingData.products.map {
            if (it.id == product.id) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it
            }
        }

        _pagingData.value = currentPagingData.copy(products = updatedProducts)
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

        isAllChecked.value = currentProducts.isNotEmpty() && currentProducts.all { it.isChecked }
    }

    private fun loadCartProducts(page: Int = INITIAL_PAGE, pageSize: Int = PAGE_SIZE) {
        cartRepository.getCartItems(page, pageSize) { result ->
            result.onSuccess { pagingData ->
                if (pagingData.products.isEmpty() && pagingData.page > 0) {
                    loadCartProducts(page = pagingData.page - 1)
                    isAllChecked.value =
                        pagingData.products.isNotEmpty() && pagingData.products.all { it.isChecked }
                } else {
                    val checkedProductIds = _checkedProducts.value?.map { it.id } ?: emptyList()
                    val updatedProducts = pagingData.products.map { product ->
                        product.copy(isChecked = product.id in checkedProductIds)
                    }

                    _isNextButtonEnabled.postValue(pagingData.hasNext)
                    _isPrevButtonEnabled.postValue(pagingData.hasPrevious)

                    isAllChecked.value = false
                    _pagingData.postValue(pagingData.copy(products = updatedProducts))
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

        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CartViewModel(
                    RepositoryProvider.cartItemRepository,
                )
            }
        }
    }
}
