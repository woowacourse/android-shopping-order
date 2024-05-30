package woowacourse.shopping.view.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repository.ShoppingCartRepositoryImpl.Companion.DEFAULT_ITEM_SIZE
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
import woowacourse.shopping.view.products.ProductListEvent

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
) : ViewModel() {
    val shoppingCart = ShoppingCart()
    private val totalItemSize: Int get() = shoppingCart.cartItems.value?.size ?: DEFAULT_ITEM_SIZE

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
    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice
    private val _totalCount: MutableLiveData<Int> = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    fun deleteShoppingCartItem(
        cartItemId: Long,
        productId: Long,
    ) {
        try {
            shoppingCartRepository.deleteCartItem(cartItemId)
            shoppingCart.deleteProduct(cartItemId)
            _shoppingCartEvent.value =
                ShoppingCartEvent.UpdateProductEvent.DELETE(productId = productId)
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

    fun loadPagingCartItemList() {
        _loadingEvent.setValue(ShoppingCartEvent.LoadCartItemList.Loading)
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val pagingData =
                    shoppingCartRepository.loadPagingCartItems(
                        totalItemSize,
                        LOAD_SHOPPING_ITEM_SIZE
                    )
                _loadingEvent.setValue(ShoppingCartEvent.LoadCartItemList.Success)
                shoppingCart.addProducts(pagingData)
            } catch (e: Exception) {
                when (e) {
                    is NoSuchDataException ->
                        _errorEvent.setValue(ShoppingCartEvent.LoadCartItemList.Fail)

                    else ->
                        _errorEvent.setValue(
                            ShoppingCartEvent.ErrorState.NotKnownError,
                        )
                }
            }
        }, 1000)
    }

    fun increaseCartItem(product: Product) {
        updateCartItem(product, UpdateCartItemType.INCREASE)
    }

    fun decreaseCartItem(product: Product) {
        updateCartItem(product, UpdateCartItemType.DECREASE)
    }

    private fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ) {
        try {
            val updateCartItemResult =
                shoppingCartRepository.updateCartItem(
                    product,
                    updateCartItemType,
                )
            when (updateCartItemResult) {
                UpdateCartItemResult.ADD -> throw NoSuchDataException()
                is UpdateCartItemResult.DELETE ->
                    deleteShoppingCartItem(
                        updateCartItemResult.cartItemId,
                        productId = product.id,
                    )

                is UpdateCartItemResult.UPDATED -> {
                    product.updateCartItemCount(updateCartItemResult.cartItemResult.counter.itemCount)
                    _shoppingCartEvent.value =
                        ShoppingCartEvent.UpdateProductEvent.Success(
                            productId = product.id,
                            count = product.cartItemCounter.itemCount,
                        )
                }
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchDataException ->
                    _errorEvent.setValue(ShoppingCartEvent.UpdateProductEvent.Fail)

                else -> _errorEvent.setValue(ShoppingCartEvent.ErrorState.NotKnownError)
            }
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
        } ?: DEFAULT_ITEM_SIZE
        _totalCount.value = checkedShoppingCart.cartItems.value?.count {
            it.cartItemSelector.isSelected
        } ?: DEFAULT_ITEM_SIZE
    }
}
