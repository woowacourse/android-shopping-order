package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.ShoppingCartRepositoryImpl.Companion.DEFAULT_ITEM_SIZE
import woowacourse.shopping.data.repository.real.RealShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_OFFSET
import woowacourse.shopping.data.repository.real.RealShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_SIZE
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.cart.model.ShoppingCart

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
) : ViewModel() {
    val shoppingCart = ShoppingCart()

    private val _shoppingCartEvent: MutableLiveData<ShoppingCartEvent.SuccessEvent> =
        MutableLiveData()
    val shoppingCartEvent: LiveData<ShoppingCartEvent.SuccessEvent> get() = _shoppingCartEvent

    private val _errorEvent: MutableSingleLiveData<ShoppingCartEvent.ErrorState> =
        MutableSingleLiveData()
    val errorEvent: SingleLiveData<ShoppingCartEvent.ErrorState> get() = _errorEvent

    private val _loadingEvent: MutableSingleLiveData<ShoppingCartEvent.LoadCartItemList> =
        MutableSingleLiveData()
    val loadingEvent: SingleLiveData<ShoppingCartEvent.LoadCartItemList> get() = _loadingEvent

    val checkedShoppingCart = ShoppingCart()

    private val _allCheck: MutableLiveData<Boolean> = MutableLiveData(false)
    val allCheck: LiveData<Boolean> get() = _allCheck
    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice
    private val _totalCount: MutableLiveData<Int> = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    fun deleteShoppingCartItem(
        cartItemId: Long,
        product: Product,
    ) {
        viewModelScope.launch {
            try {
                shoppingCartRepository.deleteCartItem(cartItemId)
                shoppingCart.deleteProduct(cartItemId)
                _shoppingCartEvent.value =
                    ShoppingCartEvent.UpdateProductEvent.DELETE(productId = product.id)
                deleteCheckedItem(CartItem(cartItemId, product))
            } catch (e: Exception) {
                when (e) {
                    is NoSuchDataException ->
                        _errorEvent.setValue(
                            ShoppingCartEvent.DeleteShoppingCart.Fail,
                        )

                    else ->
                        _errorEvent.setValue(
                            ShoppingCartEvent.ErrorState.NotKnownError,
                        )
                }
            }
        }
    }

    fun loadPagingCartItemList() {
        _loadingEvent.postValue(ShoppingCartEvent.LoadCartItemList.Loading)
        viewModelScope.launch {
            val result =
                shoppingCartRepository.loadPagingCartItems(
                    LOAD_SHOPPING_ITEM_OFFSET,
                    LOAD_SHOPPING_ITEM_SIZE,
                )

            result.onSuccess { pagingData ->
                _loadingEvent.postValue(ShoppingCartEvent.LoadCartItemList.Success)
                shoppingCart.addProducts(synchronizeLoadingData(pagingData))
                setAllCheck()
            }.onFailure { exception ->
                when (exception) {
                    is NoSuchDataException ->
                        _errorEvent.postValue(ShoppingCartEvent.LoadCartItemList.Fail)

                    else ->
                        _errorEvent.postValue(ShoppingCartEvent.ErrorState.NotKnownError)
                }
            }
        }
    }

    fun checkAllItems() {
        if (allCheck.value == true) {
            shoppingCart.cartItems.value?.forEach { cartItem ->
                if (cartItem.cartItemSelector.isSelected) {
                    deleteCheckedItem(cartItem)
                }
            }
        } else {
            shoppingCart.cartItems.value?.forEach { cartItem ->
                if (!cartItem.cartItemSelector.isSelected) {
                    addCheckedItem(cartItem)
                }
            }
        }
        updateCheckItemData()
        _shoppingCartEvent.value = ShoppingCartEvent.UpdateCheckItem.Success
    }

    fun increaseCartItem(product: Product) {
        updateCartItem(product, UpdateCartItemType.INCREASE)
    }

    fun addCheckedItem(cartItem: CartItem) {
        cartItem.cartItemSelector.selectItem()
        checkedShoppingCart.addProduct(cartItem)
        checkedShoppingCart
        updateCheckItemData()
    }

    fun deleteCheckedItem(cartItem: CartItem) {
        cartItem.cartItemSelector.unSelectItem()
        checkedShoppingCart.deleteProduct(cartItem.id)
        updateCheckItemData()
    }

    fun decreaseCartItem(product: Product) {
        updateCartItem(product, UpdateCartItemType.DECREASE)
    }

    private fun synchronizeLoadingData(pagingData: List<CartItem>): List<CartItem> {
        return if ((totalCount.value ?: DEFAULT_ITEM_SIZE) > DEFAULT_ITEM_SIZE) {
            return pagingData.map { cartItem ->
                val checkedCartItem =
                    checkedShoppingCart.cartItems.value?.find { it.id == cartItem.id }
                if (checkedCartItem != null) {
                    if (checkedCartItem.cartItemSelector.isSelected) {
                        cartItem.cartItemSelector.selectItem()
                    } else {
                        cartItem.cartItemSelector.unSelectItem()
                    }
                }
                cartItem
            }
        } else {
            pagingData
        }
    }

    private fun setAllCheck() {
        _allCheck.value = shoppingCart.cartItems.value?.all { it.cartItemSelector.isSelected }
    }

    private fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ) {
        viewModelScope.launch {
            val updateCartItemResult =
                shoppingCartRepository.updateCartItem(
                    product,
                    updateCartItemType,
                )
            updateCartItemResult.onSuccess {
                when (it) {
                    UpdateCartItemResult.ADD -> throw NoSuchDataException()
                    is UpdateCartItemResult.DELETE ->
                        deleteShoppingCartItem(
                            it.cartItemId,
                            product = product,
                        )

                    is UpdateCartItemResult.UPDATED -> {
                        product.updateCartItemCount(it.cartItemResult.counter.itemCount)
                        _shoppingCartEvent.value =
                            ShoppingCartEvent.UpdateProductEvent.Success(
                                productId = product.id,
                                count = product.cartItemCounter.itemCount,
                            )
                    }
                }
            }.onFailure {
                when (it) {
                    is NoSuchDataException ->
                        _errorEvent.setValue(ShoppingCartEvent.UpdateProductEvent.Fail)

                    else -> _errorEvent.setValue(ShoppingCartEvent.ErrorState.NotKnownError)
                }
            }
        }
    }

    private fun updateCheckItemData() {
        _totalPrice.value = checkedShoppingCart.cartItems.value?.sumOf {
            it.product.cartItemCounter.itemCount * it.product.price
        } ?: DEFAULT_ITEM_SIZE
        _totalCount.value = checkedShoppingCart.cartItems.value?.count {
            it.cartItemSelector.isSelected
        } ?: DEFAULT_ITEM_SIZE
        setAllCheck()
    }
}
