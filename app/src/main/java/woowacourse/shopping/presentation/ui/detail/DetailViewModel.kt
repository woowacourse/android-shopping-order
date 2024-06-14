package woowacourse.shopping.presentation.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.ui.SharedChangedIdsDB

class DetailViewModel(
    private val cartRepository: CartRepository,
    private val shoppingRepository: ShoppingItemsRepository,
    private val recentProductRepository: RecentProductRepository,
    val productId: Long,
) : ViewModel(), DetailEventHandler, DetailCounterHandler {
    private val _shoppingProduct = MutableLiveData<ShoppingProduct>()
    val shoppingProduct: LiveData<ShoppingProduct>
        get() = _shoppingProduct

    private val _recentProduct: MutableLiveData<RecentProduct> = MutableLiveData()
    val recentProduct: LiveData<RecentProduct>
        get() = _recentProduct

    private val _recentProductVisibility = MutableLiveData<Boolean>()
    val recentProductVisibility: LiveData<Boolean>
        get() = _recentProductVisibility

    private val _navigateToDetail = MutableLiveData<Event<Long>>()
    val navigateToDetail: LiveData<Event<Long>>
        get() = _navigateToDetail

    private val _moveBack = MutableLiveData<Event<Boolean>>()
    val moveBack: LiveData<Event<Boolean>>
        get() = _moveBack

    private val _isAddCartSuccess = MutableLiveData<Event<Boolean>>()
    val isAddCartSuccess: LiveData<Event<Boolean>>
        get() = _isAddCartSuccess

    init {
        loadShoppingProductData()
        loadRecentProductData()
        checkRecentProductVisibility()
    }

    private fun loadShoppingProductData() {
        viewModelScope.launch {
            val result = shoppingRepository.findProductItem(productId)
            result.onSuccess {
                val shoppingProduct =
                    ShoppingProduct(
                        product = it,
                        quantity = fetchQuantity(),
                    )
                _shoppingProduct.value = shoppingProduct
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private suspend fun fetchQuantity(): Int {
        return cartRepository.findCartItemWithProductId(productId)?.quantity ?: DEFAULT_QUANTITY
    }

    private fun loadRecentProductData() {
        viewModelScope.launch {
            val recentProduct = recentProductRepository.loadSecondLatest() ?: return@launch
            _recentProduct.value = recentProduct
        }
    }

    private fun checkRecentProductVisibility() {
        _recentProductVisibility.value =
            !(recentProduct.value == null || recentProduct.value?.productId == productId)
    }

    override fun onAddProductClicked(productId: Long) {
        createShoppingCartItem()
    }

    fun createShoppingCartItem() {
        val productId = shoppingProduct.value?.product?.id ?: return
        val quantity = shoppingProduct.value?.quantity() ?: return
        viewModelScope.launch {
            val result = cartRepository.addCartItem(productId, quantity)
            result.onSuccess {
                SharedChangedIdsDB.addChangedProductsId(setOf(productId))
                _isAddCartSuccess.value = Event(true)
            }.onFailure {
                _isAddCartSuccess.value = Event(false)
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    override fun onRecentProductClicked(productId: Long) {
        _navigateToDetail.value = Event(productId)
    }

    override fun onBackButtonClicked() {
        _moveBack.value = Event(true)
    }

    override fun increaseCount(
        productId: Long,
        quantity: Int,
    ) {
        val shoppingProduct = _shoppingProduct.value?.copy(quantity = quantity.inc())
        _shoppingProduct.value = shoppingProduct ?: _shoppingProduct.value
    }

    override fun decreaseCount(
        productId: Long,
        quantity: Int,
    ) {
        val shoppingProduct = _shoppingProduct.value?.copy(quantity = quantity.dec())
        _shoppingProduct.value = shoppingProduct ?: _shoppingProduct.value
    }

    companion object {
        private const val DEFAULT_QUANTITY = 1
    }
}
