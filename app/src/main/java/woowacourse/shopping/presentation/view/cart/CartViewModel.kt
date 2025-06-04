package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.data.RepositoryProvider
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
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

    val totalPrice: LiveData<Int> =
        _cartItems.map { cartItems ->
            cartItems
                .filter { cartItem -> cartItem.isSelected }
                .sumOf { selectedItem -> selectedItem.cartItem.totalPrice }
        }

    val totalCount: LiveData<Int> =
        _cartItems.map { cartItems ->
            cartItems
                .filter { cartItem -> cartItem.isSelected }
                .sumOf { selectedItem -> selectedItem.cartItem.quantity }
        }

    val allSelected: LiveData<Boolean> =
        _cartItems.map { cartItems ->
            cartItems.all { cartItem -> cartItem.isSelected }
        }

    private val _canSelectItems = MutableLiveData(true)
    val canSelectItems: LiveData<Boolean> = _canSelectItems

    private val selectionStatus = mutableMapOf<Long, Boolean>()

    private var isProcessingRequest = false

    fun loadPageOfShoppingCart(indexOffset: Int = 0) {
        val newPageIndex = ((_pageIndex.value ?: 0) + indexOffset).coerceAtLeast(0)
        cartRepository.loadPageOfCartItems(
            pageIndex = newPageIndex,
            pageSize = PAGE_SIZE,
        ) { products, isFirstPage, isLastPage ->
            val updatedItems =
                products.map { product ->
                    product
                        .toCartItemUiModel()
                        .copy(isSelected = selectionStatus[product.cartId] ?: false)
                }
            _cartItems.postValue(updatedItems)
            _pageIndex.postValue(newPageIndex)
            _isFirstPage.postValue(isFirstPage)
            _isLastPage.postValue(isLastPage)
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        if (isProcessingRequest) return

        isProcessingRequest = true
        val cartItem = product.toCartItem()
        if (product.quantity == 0) {
            addToCart(cartItem.toCartItemUiModel())
            isProcessingRequest = false
            return
        }

        cartRepository.increaseQuantity(cartItem) {
            val updatedItem =
                CartItemUiModel(
                    cartItem.copy(quantity = cartItem.quantity + 1),
                    isSelected = selectionStatus[cartItem.cartId] ?: false,
                )
            updateCartItems(updatedItem)
            isProcessingRequest = false
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        if (cartItem.quantity == 1) {
            removeFromCart(cartItem.toCartItemUiModel())
            return
        }

        if (isProcessingRequest) return
        isProcessingRequest = true
        cartRepository.decreaseQuantity(cartItem) {
            val updatedItem =
                CartItemUiModel(
                    cartItem.copy(quantity = cartItem.quantity - 1),
                    isSelected = selectionStatus[cartItem.cartId] ?: false,
                )
            updateCartItems(updatedItem)
            isProcessingRequest = false
        }
    }

    private fun addToCart(cartItem: CartItemUiModel) {
        val newItem = cartItem.cartItem.copy(quantity = 1)
        cartRepository.addCartItem(newItem) { addedItem ->
            if (addedItem != null) {
                _cartItems.postValue(_cartItems.value?.plus(cartItem.copy(cartItem = newItem)))
                _itemUpdateEvent.postValue(addedItem.toCartItemUiModel().copy(isSelected = true))
                setCartItemSelection(addedItem.toCartItemUiModel(), true)
            }
        }
    }

    fun removeFromCart(cartItem: CartItemUiModel) {
        val removedItem = cartItem.cartItem.copy(quantity = 0)
        cartRepository.deleteCartItem(removedItem.cartId) {
            _cartItems.postValue(
                _cartItems.value?.filterNot { cartItem ->
                    cartItem.cartItem.cartId == removedItem.cartId
                },
            )
            _itemDeleteEvent.postValue(removedItem.cartId)
            selectionStatus.remove(removedItem.cartId)
            fetchRecommendedProducts()
        }
    }

    private fun updateCartItems(modifiedCartItem: CartItemUiModel) {
        val newCartItems =
            _cartItems.value.orEmpty().map { cartItem ->
                if (modifiedCartItem.cartItem.cartId == cartItem.cartItem.cartId) {
                    modifiedCartItem
                } else {
                    cartItem
                }
            }
        _cartItems.postValue(newCartItems)
        _itemUpdateEvent.postValue(modifiedCartItem)
    }

    fun setCartItemSelection(
        cartItem: CartItemUiModel,
        isSelected: Boolean,
    ) {
        selectionStatus[cartItem.cartItem.cartId] = isSelected
        updateSelectionInfo()
    }

    fun selectAllCartItems(selectAll: Boolean) {
        cartRepository.loadAllCartItems { allItems ->
            _cartItems.postValue(
                allItems.map { cartItem ->
                    selectionStatus[cartItem.cartId] = selectAll
                    cartItem.toCartItemUiModel().copy(isSelected = selectAll)
                },
            )
            loadPageOfShoppingCart()
        }
    }

    fun disableSelection() {
        _canSelectItems.value = false
    }

    private fun updateSelectionInfo() {
        cartRepository.loadAllCartItems { cartItems ->
            val selectedItemIds = selectionStatus.filter { it.value }.map { it.key }.toSet()
            _cartItems.postValue(
                cartItems.map { cartItem ->
                    if (cartItem.cartId in selectedItemIds) {
                        cartItem.toCartItemUiModel().copy(isSelected = true)
                    } else {
                        cartItem.toCartItemUiModel()
                    }
                },
            )
        }
    }

    fun fetchRecommendedProducts() {
        productRepository.loadRecommendedProducts(RECOMMENDED_PRODUCTS_SIZE) { recommendedProducts ->
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
