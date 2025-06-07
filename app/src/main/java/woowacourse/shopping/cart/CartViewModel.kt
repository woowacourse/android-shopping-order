package woowacourse.shopping.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.cart.CartItem.ProductItem
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.product.catalog.ProductUiModel
import kotlin.collections.map

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartProducts = MutableLiveData<List<ProductUiModel>>(emptyList())
    val cartProducts: LiveData<List<ProductUiModel>> = _cartProducts

    private val _isNextButtonEnabled = MutableLiveData<Boolean>(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    private val _isPrevButtonEnabled = MutableLiveData<Boolean>(false)
    val isPrevButtonEnabled: LiveData<Boolean> = _isPrevButtonEnabled

    private val _page = MutableLiveData<Int>(INITIAL_PAGE)
    val page: LiveData<Int> = _page

    private val _updatedItem = MutableLiveData<ProductUiModel>()
    val updatedItem: LiveData<ProductUiModel> = _updatedItem

    private val _loadingState: MutableLiveData<Boolean> =
        MutableLiveData(true)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _totalPurchaseCount = MutableLiveData<Int>(INITIAL_CART_ITEM_COUNT)
    val totalPurchaseCount: LiveData<Int> get() = _totalPurchaseCount

    private val _totalPurchaseAmount = MutableLiveData<Int>(0)
    val totalPurchaseAmount: LiveData<Int> get() = _totalPurchaseAmount

    private val _allChecked = MutableLiveData<Boolean>(true)
    val allChecked: LiveData<Boolean> get() = _allChecked

    init {
        loadCartProducts()
    }

    fun onAllSelectedProducts() {
        val isChecked = _allChecked.value ?: true
        _cartProducts.value =
            _cartProducts.value?.map {
                it.copy(isChecked = !isChecked)
            }
        _allChecked.value = !isChecked
        fetchTotalPurchaseAmount()
    }

    fun fetchTotalPurchaseAmount() {
        val amount =
            _cartProducts.value?.filter { it.isChecked == true }?.sumOf { it.price * it.quantity }
                ?: 0
        _totalPurchaseAmount.postValue(amount)
    }

    fun fetchTotalPurchaseCount() {
        val counts = _cartProducts.value?.count { it.isChecked == true } ?: 0
        _totalPurchaseCount.postValue(counts)
    }

    fun deleteCartProduct(cartProduct: ProductItem) {
        cartRepository.deleteCartProduct(cartProduct.productItem) { result ->
            if (result) {
                val newCartProducts =
                    _cartProducts.value?.filterNot { it == cartProduct.productItem } ?: emptyList()
                _cartProducts.postValue(newCartProducts)
                val currentPage = page.value ?: INITIAL_PAGE
                val startIndex = currentPage * PAGE_SIZE
                if (startIndex >= newCartProducts.size && currentPage > 0) {
                    decreasePage()
                }
                loadCartProducts()
            }
        }
    }

    fun onPaginationButtonClick(buttonDirection: Int) {
        cartRepository.getTotalProductsCount { totalSize ->
            val currentPage = page.value ?: INITIAL_PAGE
            val lastPage = (totalSize - 1) / PAGE_SIZE

            when (buttonDirection) {
                PREV_BUTTON -> {
                    if (currentPage > 0) {
                        decreasePage()
                        loadCartProducts()
                    }
                }

                NEXT_BUTTON -> {
                    if (currentPage < lastPage) {
                        increasePage()
                        loadCartProducts()
                    }
                }
            }
        }
    }

    fun updateQuantity(
        event: Int,
        product: ProductUiModel,
    ) {
        when (event) {
            DECREASE_BUTTON -> decreaseQuantity(product)
            INCREASE_BUTTON -> increaseQuantity(product)
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        cartRepository.updateCartProduct(
            product,
            product.quantity + A_COUNT,
        ) { success ->
            if (success) {
                val newProduct = product.copy(quantity = product.quantity + A_COUNT)
                updateItem(newProduct)
            }
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        if (product.quantity > INITIAL_PRODUCT_COUNT) {
            val newProduct = product.copy(quantity = product.quantity - A_COUNT)
            cartRepository.updateCartProduct(
                product,
                product.quantity - A_COUNT,
            ) { success ->
                if (success) {
                    updateItem(newProduct)
                }
            }
        }
    }

    private fun updateItem(newProduct: ProductUiModel) {
        _updatedItem.postValue(newProduct)
        _cartProducts.value =
            _cartProducts.value?.map {
                if (it.id == newProduct.id) newProduct else it
            }
        fetchTotalPurchaseAmount()
        fetchTotalPurchaseCount()
    }

    fun changeProductSelection(product: ProductUiModel) {
        val newProduct = product.copy(isChecked = !product.isChecked ?: true)
        _cartProducts.value =
            _cartProducts.value?.map {
                if (it.id == newProduct.id) newProduct else it
            }
        _cartProducts.value?.let { list ->
            if (list.isEmpty()) return@let
            val firstChecked = list[0].isChecked ?: false
            val allSame = list.all { (it.isChecked ?: false) == firstChecked }
            if (allSame) {
                _allChecked.value = firstChecked
            }
        }
        fetchTotalPurchaseAmount()
        fetchTotalPurchaseCount()
    }

    private fun checkNextButtonEnabled(totalSize: Int) {
        val currentPage = page.value ?: INITIAL_PAGE
        val lastPage = (totalSize - 1) / PAGE_SIZE
        _isNextButtonEnabled.postValue(currentPage < lastPage)
    }

    private fun checkPrevButtonEnabled() {
        val currentPage = page.value ?: INITIAL_PAGE
        _isPrevButtonEnabled.postValue(currentPage >= 1)
    }

    private fun increasePage() {
        _page.postValue(page.value?.plus(1))
    }

    private fun decreasePage() {
        _page.postValue(page.value?.minus(1))
    }

    private fun loadCartProducts(pageSize: Int = PAGE_SIZE) {
        cartRepository.getTotalProductsCount { totalSize ->
            _totalPurchaseCount.postValue(totalSize)
            val currentPage = page.value ?: INITIAL_PAGE
            val startIndex = currentPage * pageSize
            if (startIndex >= totalSize) {
                return@getTotalProductsCount
            }
            cartRepository
                .getCartProductsInRange(currentPage, pageSize) { cartProducts ->
                    val pagedProducts: List<ProductUiModel> =
                        cartProducts.map {
                            it.copy(isChecked = true)
                        }
                    _cartProducts.postValue(pagedProducts)
                    checkNextButtonEnabled(totalSize)
                    checkPrevButtonEnabled()
                    _loadingState.postValue(false)
                    val amount =
                        pagedProducts.filter { it.isChecked == true }.sumOf { it.price * it.quantity }
                    _totalPurchaseAmount.postValue(amount)
                }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val INITIAL_PAGE = 0
        private const val PREV_BUTTON = 1
        private const val NEXT_BUTTON = 2
        private const val DECREASE_BUTTON = 0
        private const val INCREASE_BUTTON = 1
        private const val INITIAL_CART_ITEM_COUNT = 0
        private const val INITIAL_PRODUCT_COUNT: Int = 1
        private const val A_COUNT: Int = 1
    }
}
