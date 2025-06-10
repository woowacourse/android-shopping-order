package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemsRepository
import woowacourse.shopping.presentation.cart.event.CartEventHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.util.SingleLiveEvent

class CartViewModel(
    private val cartRepository: CartItemsRepository,
) : ViewModel(),
    CartEventHandler {
    private val _pageEvent = SingleLiveEvent<Int>()
    val pageEvent: LiveData<Int> = _pageEvent

    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _pagingData = MutableLiveData<PagingData>()
    val pagingData: LiveData<PagingData> = _pagingData

    private val _checkedProducts = MutableLiveData<List<ProductUiModel>>(emptyList())
    val checkedProducts: LiveData<List<ProductUiModel>> = _checkedProducts

    val totalOrderPrice: LiveData<Int> =
        _checkedProducts.map { products ->
            products.sumOf { it.price * it.quantity }
        }

    val checkedProductCount: LiveData<Int> =
        _checkedProducts.map { products ->
            products.sumOf { it.quantity }
        }

    val isAllChecked = MutableLiveData(false)

    private var currentPage: Int = INITIAL_PAGE

    init {
        loadCartProducts()
    }

    override fun onDeleteProduct(cartProduct: ProductUiModel) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(cartProduct.id)
                .onSuccess {
                    loadCartProducts()
                    setCheckedProducts(cartProduct)
                }
        }
    }

    private fun setCheckedProducts(cartProduct: ProductUiModel) {
        val currentCheckedProducts = _checkedProducts.value ?: emptyList()
        _checkedProducts.value = currentCheckedProducts.filterNot { it.id == cartProduct.id }
    }

    override fun onNextPage() {
        if (currentPage >= INITIAL_PAGE && _pagingData.value?.hasNext == true) {
            currentPage++
            _pageEvent.value = currentPage
            loadCartProducts()
            isAllChecked.value = false
        }
    }

    override fun onPrevPage() {
        if (currentPage > INITIAL_PAGE) {
            currentPage--
            _pageEvent.value = currentPage
            loadCartProducts()
            isAllChecked.value = false
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        viewModelScope.launch {
            val newProduct = product.copy(quantity = product.quantity + 1)
            cartRepository.updateCartItemQuantity(newProduct.id, newProduct.quantity)
                .onSuccess {
                    _product.value = newProduct
                    updateProductInPagingData(newProduct)
                    setOrderData()
                }
                .onFailure {
                }
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        viewModelScope.launch {
            val newQuantity = (product.quantity - 1).coerceAtLeast(1)
            val newProduct = product.copy(quantity = newQuantity)
            cartRepository.updateCartItemQuantity(newProduct.id, newProduct.quantity)
                .onSuccess {
                    _product.value = newProduct
                    updateProductInPagingData(newProduct)
                    setOrderData()
                }
                .onFailure {
                }
        }
    }

    override fun isNextButtonEnabled(): Boolean = _pagingData.value?.hasNext == true

    override fun isPrevButtonEnabled(): Boolean = _pagingData.value?.hasPrevious == true

    override fun isPaginationEnabled(): Boolean = isNextButtonEnabled() && isPrevButtonEnabled()

    override fun getPage(): Int = currentPage

    override fun onCheckProduct(product: ProductUiModel) {
        val currentPagingData = _pagingData.value ?: return

        val updateProducts =
            currentPagingData.products.map {
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
            val updatedProducts =
                currentPagingData.products.map {
                    it.copy(isChecked = isAllChecked.value ?: it.isChecked)
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

        _checkedProducts.value = newCheckedProducts
        isAllChecked.value = currentProducts.isNotEmpty() && currentProducts.all { it.isChecked }
    }

    private fun loadCartProducts(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch {
            cartRepository.getCartItems(currentPage, pageSize)
                .onSuccess { pagingData ->
                    if (pagingData.products.isEmpty() && currentPage > INITIAL_PAGE) {
                        currentPage--
                        _pageEvent.value = currentPage
                        loadCartProducts()
                        isAllChecked.value =
                            pagingData.products.isNotEmpty() && pagingData.products.all { it.isChecked }
                    } else {
                        val checkedProductIds = _checkedProducts.value?.map { it.id } ?: emptyList()
                        val updatedProducts =
                            pagingData.products.map { product ->
                                product.copy(isChecked = product.id in checkedProductIds)
                            }
                        isAllChecked.value = false
                        _pagingData.value = pagingData.copy(products = updatedProducts)
                    }
                }
        }
    }

    private fun updateProductInPagingData(updatedProduct: ProductUiModel) {
        val currentPagingData = _pagingData.value ?: return
        _pagingData.value =
            currentPagingData.copy(
                products = currentPagingData.products.update(updatedProduct),
            )
        _checkedProducts.value = _checkedProducts.value?.update(updatedProduct)
    }

    private fun List<ProductUiModel>.update(updated: ProductUiModel): List<ProductUiModel> {
        return map { if (it.id == updated.id) updated else it }
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
