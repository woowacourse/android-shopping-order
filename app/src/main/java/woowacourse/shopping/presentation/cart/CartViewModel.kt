package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.presentation.cart.event.CartEventHandler
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.util.SingleLiveEvent

class CartViewModel(
    private val repository: CartItemRepository,
) : ViewModel(),
    CartEventHandler {
    private val _cartProducts = MutableLiveData<List<ProductUiModel>>()
    val cartProducts: LiveData<List<ProductUiModel>> = _cartProducts

    private val _isNextButtonEnabled = MutableLiveData<Boolean>(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    private val _isPrevButtonEnabled = MutableLiveData<Boolean>(false)
    val isPrevButtonEnabled: LiveData<Boolean> = _isPrevButtonEnabled

    private val _pageEvent = SingleLiveEvent<Int>()
    val pageEvent: LiveData<Int> = _pageEvent

    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private var currentPage: Int = INITIAL_PAGE

    init {
        loadCartProducts()
    }

    override fun onDeleteProduct(cartProduct: ProductUiModel) {
//        repository.deleteCartItemById(cartProduct.id) {
//            loadCartProducts()
//        }
    }

    override fun onNextPage() {
//        repository.getAllCartItemSize { size ->
//            val lastPage = (size - 1) / PAGE_SIZE
//            if (currentPage < lastPage) {
//                currentPage++
//                _pageEvent.postValue(currentPage)
//                loadCartProducts()
//            }
//        }
    }

    override fun onPrevPage() {
        if (currentPage > 0) {
            currentPage--
            _pageEvent.postValue(currentPage)
            loadCartProducts()
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
//        val newProduct = product.copy(quantity = product.quantity + 1)
//        repository.updateCartItem(newProduct) {
//            _product.postValue(newProduct)
//        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
//        val newQuantity = if (product.quantity > 1) product.quantity - 1 else 1
//        val newProduct = product.copy(quantity = newQuantity)
//        repository.updateCartItem(newProduct) {
//            _product.postValue(newProduct)
//        }
    }

    override fun isNextButtonEnabled(): Boolean = _isNextButtonEnabled.value == true

    override fun isPrevButtonEnabled(): Boolean = _isPrevButtonEnabled.value == true

    override fun isPaginationEnabled(): Boolean = (_isNextButtonEnabled.value == true) || (_isPrevButtonEnabled.value == true)

    override fun getPage(): Int = currentPage

    private fun loadCartProducts(pageSize: Int = PAGE_SIZE) {
//        repository.getAllCartItemSize { totalSize ->
//            var current = currentPage
//            while (current > 0 && current * pageSize >= totalSize) {
//                current--
//            }
//            currentPage = current
//            _pageEvent.postValue(current)
//
//            val startIndex = current * pageSize
//            val endIndex = minOf(startIndex + pageSize, totalSize)
//
//            repository.subListCartItems(startIndex, endIndex) { products ->
//                _cartProducts.postValue(products)
//                _isNextButtonEnabled.postValue(current < (totalSize - 1) / pageSize)
//                _isPrevButtonEnabled.postValue(current > 0)
//            }
//        }
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val INITIAL_PAGE = 0

        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CartViewModel(
                    RepositoryProvider.cartItemRepository
                )
            }
        }
    }
}
