package woowacourse.shopping.presentation.view.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> = _totalCount

    private val _allSelected = MutableLiveData<Boolean>()
    val allSelected: LiveData<Boolean> = _allSelected

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
            updateSelectionInfo()
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
                _itemUpdateEvent.postValue(addedItem.toCartItemUiModel().copy(isSelected = true))
                setCartItemSelection(addedItem.toCartItemUiModel(), true)
            }
        }
    }

    fun removeFromCart(cartItem: CartItemUiModel) {
        val removedItem = cartItem.cartItem.copy(quantity = 0)
        cartRepository.deleteCartItem(removedItem.cartId) {
            _itemDeleteEvent.postValue(removedItem.cartId)
            selectionStatus.remove(removedItem.cartId)
            updateSelectionInfo()
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
        val asdf =
            _cartItems.value?.map { item ->
                if (item.cartItem.cartId == cartItem.cartItem.cartId) {
                    item.copy(isSelected = isSelected)
                } else {
                    item
                }
            }
        _cartItems.postValue(
            _cartItems.value?.map { item ->
                if (item.cartItem.cartId == cartItem.cartItem.cartId) {
                    item.copy(isSelected = isSelected)
                } else {
                    item
                }
            },
        )
        Log.wtf("asdf", "$asdf")
        updateSelectionInfo()
    }

    fun setAllSelections(selectAll: Boolean) {
        cartRepository.loadAllCartItems { allItems ->
            allItems.forEach { cartItem ->
                selectionStatus[cartItem.cartId] = selectAll
                _cartItems.postValue(
                    _cartItems.value?.map { item ->
                        if (item.cartItem.cartId == cartItem.cartId) {
                            item.copy(isSelected = selectAll)
                        } else {
                            item
                        }
                    },
                )
            }
            updateSelectionInfo()
            loadPageOfShoppingCart()
        }
    }

    fun disableSelection() {
        _canSelectItems.value = false
    }

    fun fetchRecommendedProducts() {
        productRepository.loadRecommendedProducts(RECOMMENDED_PRODUCTS_SIZE) { recommendedProducts ->
            _recommendedProducts.postValue(recommendedProducts.map(Product::toProductUiModel))
        }
    }

    private fun updateSelectionInfo() {
        cartRepository.loadAllCartItems { cartItems ->
            val selectedItemIds = selectionStatus.filter { it.value }.map { it.key }.toSet()
            val selectedItems = cartItems.filter { selectedItemIds.contains(it.cartId) }
            _totalPrice.postValue(selectedItems.sumOf { it.totalPrice })
            _totalCount.postValue(selectedItems.sumOf { it.quantity })
            _allSelected.postValue(selectedItems.isNotEmpty() && selectedItems.size == cartItems.size)
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
