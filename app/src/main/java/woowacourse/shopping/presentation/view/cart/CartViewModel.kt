package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RepositoryProvider
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Product
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.model.toProductUiModel

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItemUiModel>>()
    val cartItems: LiveData<List<CartItemUiModel>> = _cartItems

    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>()
    val recommendedProducts: LiveData<List<ProductUiModel>> = _recommendedProducts

    private val _itemDeleteEvent = MutableLiveData<Long>()
    val itemDeleteEvent: LiveData<Long> = _itemDeleteEvent

    private val _itemUpdateEvent = MutableLiveData<CartItemUiModel>()
    val itemUpdateEvent: LiveData<CartItemUiModel> = _itemUpdateEvent

    private val _pageIndex = MutableLiveData(INITIAL_PAGE_INDEX)
    val pageIndex: LiveData<Int> = _pageIndex

    private var _isFirstPage = MutableLiveData<Boolean>()
    val isFirstPage: LiveData<Boolean> = _isFirstPage

    private var _isLastPage = MutableLiveData<Boolean>()
    val isLastPage: LiveData<Boolean> = _isLastPage

    private val _totalPrice = MutableLiveData<Long>(0)
    val totalPrice: LiveData<Long> = _totalPrice

    private val _totalCount = MutableLiveData<Int>(0)
    val totalCount: LiveData<Int> = _totalCount

    private val _allSelected = MutableLiveData<Boolean>(false)
    val allSelected: LiveData<Boolean> = _allSelected

    private val _selectedProductIds = MutableLiveData<Set<Long>>()
    val selectedProductIds: LiveData<Set<Long>> = _selectedProductIds

    val canPlaceOrder: LiveData<Boolean> =
        _selectedProductIds.map { selectedProductIds -> selectedProductIds.isNotEmpty() }

    private val _canSelectItems = MutableLiveData(true)
    val canSelectItems: LiveData<Boolean> = _canSelectItems

    private var isProcessingRequest = false

    fun loadPageOfShoppingCart(indexOffset: Int = 0) {
        viewModelScope.launch {
            val newPageIndex = ((_pageIndex.value ?: 0) + indexOffset).coerceAtLeast(0)
            val page =
                cartRepository.loadPageOfCartItems(
                    pageIndex = newPageIndex,
                    pageSize = PAGE_SIZE,
                )
            val updatedItems =
                page.items.map { product ->
                    product
                        .toCartItemUiModel()
                        .copy(
                            isSelected =
                                _selectedProductIds.value.orEmpty()
                                    .contains(product.product.id),
                        )
                }
            _cartItems.postValue(updatedItems)
            _pageIndex.postValue(newPageIndex)
            _isFirstPage.postValue(page.isFirst)
            _isLastPage.postValue(page.isLast)
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        viewModelScope.launch {
            if (isProcessingRequest) return@launch

            isProcessingRequest = true
            val cartItem = product.toCartItem()
            if (product.quantity == 0) {
                addToCart(cartItem.toCartItemUiModel())
                isProcessingRequest = false
                return@launch
            }

            cartRepository.increaseQuantity(cartItem)
            _itemUpdateEvent.value =
                CartItemUiModel(
                    cartItem.copy(quantity = cartItem.quantity + 1),
                    isSelected = _selectedProductIds.value.orEmpty().contains(product.id),
                )
            updateSelectionInfo()
            loadPageOfShoppingCart()

            isProcessingRequest = false
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        viewModelScope.launch {
            if (isProcessingRequest) return@launch

            isProcessingRequest = true
            val cartItem = product.toCartItem()
            if (cartItem.quantity == 1) {
                removeFromCart(cartItem.toCartItemUiModel())
                isProcessingRequest = false
                return@launch
            }

            cartRepository.decreaseQuantity(cartItem)
            _itemUpdateEvent.value =
                CartItemUiModel(
                    cartItem.copy(quantity = cartItem.quantity - 1),
                    isSelected = _selectedProductIds.value.orEmpty().contains(product.id),
                )
            updateSelectionInfo()
            loadPageOfShoppingCart()

            isProcessingRequest = false
        }
    }

    private suspend fun addToCart(cartItem: CartItemUiModel) {
        val newItem = cartItem.cartItem.copy(quantity = 1)
        cartRepository.addCartItem(newItem)?.let { addedItem ->
            _cartItems.postValue(_cartItems.value?.plus(cartItem.copy(cartItem = newItem)))
            _itemUpdateEvent.postValue(addedItem.toCartItemUiModel().copy(isSelected = true))
            setCartItemSelection(addedItem.toCartItemUiModel(), true)
        }
    }

    fun removeFromCart(cartItem: CartItemUiModel) {
        viewModelScope.launch {
            val removedItem = cartItem.cartItem.copy(quantity = 0)
            cartRepository.deleteCartItem(removedItem.cartId)
            _cartItems.postValue(
                _cartItems.value?.filterNot { item ->
                    item.cartItem.cartId == removedItem.cartId
                },
            )
            _itemDeleteEvent.postValue(removedItem.cartId)
            _itemUpdateEvent.postValue(removedItem.toCartItemUiModel().copy(isSelected = false))
            _selectedProductIds.value =
                _selectedProductIds.value.orEmpty().toMutableSet()
                    .apply { remove(removedItem.product.id) }
            updateSelectionInfo()
        }
    }

    fun setCartItemSelection(
        cartItem: CartItemUiModel,
        isSelected: Boolean,
    ) {
        viewModelScope.launch {
            _selectedProductIds.value =
                _selectedProductIds.value.orEmpty().toMutableSet().apply {
                    if (isSelected) {
                        add(cartItem.cartItem.product.id)
                    } else {
                        remove(cartItem.cartItem.product.id)
                    }
                }
            updateSelectionInfo()
        }
    }

    fun selectAllCartItems(selectAll: Boolean) {
        viewModelScope.launch {
            _selectedProductIds.value =
                if (selectAll) {
                    cartRepository.loadAllCartItems().map { cartItem -> cartItem.product.id }
                        .toSet()
                } else {
                    emptySet()
                }
            loadPageOfShoppingCart()
            updateSelectionInfo()
        }
    }

    private suspend fun updateSelectionInfo() {
        val allCartItems = cartRepository.loadAllCartItems()
        val selectedCartItems =
            allCartItems.filter { cartItem ->
                _selectedProductIds.value.orEmpty().contains(cartItem.product.id)
            }
        _totalCount.value = selectedCartItems.size
        _totalPrice.value = selectedCartItems.sumOf(CartItem::totalPrice)
        _allSelected.value =
            selectedCartItems.isNotEmpty() && allCartItems.size == selectedCartItems.size
    }

    fun disableSelection() {
        _canSelectItems.value = false
    }

    fun fetchRecommendedProducts() {
        viewModelScope.launch {
            val recommendedProducts =
                productRepository.loadRecommendedProducts(RECOMMENDED_PRODUCTS_SIZE)
            _recommendedProducts.postValue(recommendedProducts.map(Product::toProductUiModel))
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 0
        private const val PAGE_SIZE = 5
        private const val RECOMMENDED_PRODUCTS_SIZE = 10

        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val productRepository = RepositoryProvider.productRepository
                    val cartRepository = RepositoryProvider.cartRepository
                    return CartViewModel(productRepository, cartRepository) as T
                }
            }
    }
}
