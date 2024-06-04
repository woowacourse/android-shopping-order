package woowacourse.shopping.view.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repository.ProductRepositoryImpl.Companion.DEFAULT_ITEM_SIZE
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_OFFSET
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_SIZE
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
) : ViewModel(), OnClickCartItemCounter {
    val shoppingCart = ShoppingCart()

    private val _shoppingCartEvent: MutableLiveData<ShoppingCartEvent.SuccessEvent> = MutableLiveData()
    val shoppingCartEvent: LiveData<ShoppingCartEvent.SuccessEvent> get() = _shoppingCartEvent

    private val _errorEvent: MutableSingleLiveData<ShoppingCartEvent.ErrorState> = MutableSingleLiveData()
    val errorEvent: SingleLiveData<ShoppingCartEvent.ErrorState> get() = _errorEvent

    private val _loadingEvent: MutableSingleLiveData<ShoppingCartEvent.LoadCartItemList> = MutableSingleLiveData()
    val loadingEvent: SingleLiveData<ShoppingCartEvent.LoadCartItemList> get() = _loadingEvent

    val checkedShoppingCart = ShoppingCart()

    private val _allCheck: MutableLiveData<Boolean> = MutableLiveData(false)
    val allCheck: LiveData<Boolean> get() = _allCheck
    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice
    private val _totalCount: MutableLiveData<Int> = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    fun deleteShoppingCartItem(cartItem: CartItem) {
        shoppingCartRepository.deleteCartItem(cartItem.id).onSuccess { _ ->
            shoppingCart.deleteProduct(cartItem.id)
            deleteCheckedItem(CartItem(cartItem.id, cartItem.product))
        }.onFailure { e ->
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

    fun loadPagingCartItemList() {
        _loadingEvent.setValue(ShoppingCartEvent.LoadCartItemList.Loading)
        shoppingCartRepository.loadPagingCartItems(
            LOAD_SHOPPING_ITEM_OFFSET,
            LOAD_SHOPPING_ITEM_SIZE,
        ).onSuccess { pagingData ->
            shoppingCart.addProducts(synchronizeLoadingData(pagingData))
            setAllCheck()
        }.onFailure { e ->
            _loadingEvent.setValue(ShoppingCartEvent.LoadCartItemList.Fail)
            when (e) {
                is NoSuchDataException ->
                    _errorEvent.setValue(ShoppingCartEvent.LoadCartItemList.Fail)

                else ->
                    _errorEvent.setValue(
                        ShoppingCartEvent.ErrorState.NotKnownError,
                    )
            }
        }
    }

    private fun setAllCheck() {
        _allCheck.value = shoppingCart.cartItems.value?.all { it.cartItemSelector.isSelected }
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

    fun addCheckedItem(cartItem: CartItem) {
        cartItem.cartItemSelector.selectItem()
        checkedShoppingCart.addProduct(cartItem)
        updateCheckItemData()
    }

    fun deleteCheckedItem(cartItem: CartItem) {
        cartItem.cartItemSelector.unSelectItem()
        checkedShoppingCart.deleteProduct(cartItem.id)
        updateCheckItemData()
    }

    private fun updateCheckItemData() {
        _totalPrice.value = checkedShoppingCart.cartItems.value?.sumOf {
            it.product.cartItemCounter.itemCount * it.product.price
        } ?: DEFAULT_TOTAL_PRICE
        _totalCount.value = checkedShoppingCart.cartItems.value?.count {
            it.cartItemSelector.isSelected
        } ?: DEFAULT_TOTAL_COUNT
        setAllCheck()
    }

    override fun clickIncrease(product: Product) {
        shoppingCartRepository.increaseCartItem(product).onSuccess {
            shoppingCart.cartItems.value?.find { it.product.id == product.id }?.let { cartItem ->
                Log.d("ShoppingCartViewModel", "clickIncrease1: ${cartItem.product.id}")
                cartItem.product.cartItemCounter.increase()
                Log.d("ShoppingCartViewModel", "clickIncrease: ${cartItem.product.cartItemCounter.itemCount}")
            }
            shoppingCart.updateProducts(shoppingCart.cartItems.value!!)
        }.onFailure { e ->
            when (e) {
                is NoSuchDataException ->
                    _errorEvent.setValue(ShoppingCartEvent.UpdateProductEvent.Fail)

                else ->
                    _errorEvent.setValue(ShoppingCartEvent.ErrorState.NotKnownError)
            }
        }
    }

    override fun clickDecrease(product: Product) {
        shoppingCartRepository.decreaseCartItem(product).onSuccess {
            shoppingCart.cartItems.value?.find { it.product.id == product.id }?.let { cartItem ->
                cartItem.product.cartItemCounter.decrease()
            }

            shoppingCart.updateProducts(shoppingCart.cartItems.value!!)
        }.onFailure {
            when (it) {
                is NoSuchDataException ->
                    _errorEvent.setValue(ShoppingCartEvent.UpdateProductEvent.Fail)

                else ->
                    _errorEvent.setValue(ShoppingCartEvent.ErrorState.NotKnownError)
            }
        }
    }

    companion object {
        private const val DEFAULT_TOTAL_PRICE = 0
        private const val DEFAULT_TOTAL_COUNT = 0
    }
}
