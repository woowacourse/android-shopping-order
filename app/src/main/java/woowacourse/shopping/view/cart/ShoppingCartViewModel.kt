package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.ShoppingCartRepositoryImpl.Companion.DEFAULT_ITEM_SIZE
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_OFFSET
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_SIZE
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.view.base.BaseViewModel
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
) : BaseViewModel(), OnClickCartItemCounter, OnClickShoppingCart {
    val shoppingCart = ShoppingCart()

    private val _shoppingCartEvent: MutableLiveData<ShoppingCartEvent> = MutableLiveData()
    val shoppingCartEvent: LiveData<ShoppingCartEvent> get() = _shoppingCartEvent

    private val checkedShoppingCart = ShoppingCart()

    private val _allCheck: MutableLiveData<Boolean> = MutableLiveData(false)
    val allCheck: LiveData<Boolean> get() = _allCheck
    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice
    private val _totalCount: MutableLiveData<Int> = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            handleException(throwable)
        }

    fun loadPagingCartItemList() {
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                shoppingCartRepository.loadPagingCartItems(
                    LOAD_SHOPPING_ITEM_OFFSET,
                    LOAD_SHOPPING_ITEM_SIZE,
                ).getOrThrow()
            }.onSuccess { pagingData ->
                shoppingCart.addProducts(synchronizeLoadingData(pagingData))
                setAllCheck()
            }.onFailure {
                handleException(it)
            }
        }
    }

    private fun setAllCheck() {
        _allCheck.value = shoppingCart.cartItems.value?.all { it.cartItemSelector.isSelected }
    }

    private fun toggleAllItems() {
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
    }

    private fun synchronizeLoadingData(pagingData: List<CartItem>): List<CartItem> {
        return if ((totalCount.value ?: DEFAULT_ITEM_SIZE) > DEFAULT_ITEM_SIZE) {
            pagingData.map { cartItem ->
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

    private fun addCheckedItem(cartItem: CartItem) {
        cartItem.cartItemSelector.selectItem()
        checkedShoppingCart.addProduct(cartItem)
        updateCheckItemData()
    }

    private fun deleteShoppingCartItem(
        cartItemId: Long,
        product: Product,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                shoppingCartRepository.deleteCartItem(cartItemId).getOrThrow()
            }.onSuccess {
                shoppingCart.deleteProduct(cartItemId)
                _shoppingCartEvent.value =
                    ShoppingCartEvent.UpdateProductEvent.DELETE(productId = product.id)
                deleteCheckedItem(CartItem(cartItemId, product))
            }.onFailure {
                handleException(it)
            }
        }
    }

    private fun deleteCheckedItem(cartItem: CartItem) {
        cartItem.cartItemSelector.unSelectItem()
        checkedShoppingCart.deleteProduct(cartItem.id)
        updateCheckItemData()
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

    override fun clickIncrease(product: Product) {
        viewModelScope.launch(coroutineExceptionHandler) {
            shoppingCartRepository.increaseCartItem(product)
                .onSuccess {
                    _shoppingCartEvent.value =
                        ShoppingCartEvent.UpdateProductEvent.Success(
                            productId = product.id,
                            count = product.cartItemCounter.itemCount,
                        )
                }.onFailure {
                    handleException(it)
                }
        }
    }

    override fun clickDecrease(product: Product) {
        viewModelScope.launch(coroutineExceptionHandler) {
            product.cartItemCounter.decrease()
            shoppingCartRepository.decreaseCartItem(product)
                .onSuccess {
                    if (product.cartItemCounter.itemCount == 0) {
                        _shoppingCartEvent.value =
                            ShoppingCartEvent.UpdateProductEvent.DELETE(productId = product.id)
                        shoppingCart.deleteProductFromProductId(product.id)
                    } else {
                        _shoppingCartEvent.value =
                            ShoppingCartEvent.UpdateProductEvent.Success(
                                productId = product.id,
                                count = product.cartItemCounter.itemCount,
                            )
                    }
                }.onFailure {
                    handleException(it)
                }
        }
    }

    override fun clickRemoveCartItem(cartItem: CartItem) {
        deleteShoppingCartItem(
            cartItemId = cartItem.id,
            product = cartItem.product,
        )
    }

    override fun clickCheckBox(cartItem: CartItem) {
        if (cartItem.cartItemSelector.isSelected) {
            deleteCheckedItem(cartItem)
        } else {
            addCheckedItem(cartItem)
        }
    }

    override fun clickToggleAll() {
        toggleAllItems()
    }

    override fun clickOrder() {
        _shoppingCartEvent.value = ShoppingCartEvent.SendCartItem.Success(checkedShoppingCart)
    }
}
