package woowacourse.shopping.presentation.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event

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
        val shoppingProduct =
            ShoppingProduct(
                product = shoppingRepository.findProductItem(productId) ?: return,
                quantity = fetchQuantity(),
            )
        _shoppingProduct.value = shoppingProduct
    }

    private fun fetchQuantity(): Int {
        return cartRepository.findCartItemWithProductId(productId)?.quantity ?: 1
    }

    private fun loadRecentProductData() {
        val recentProduct = recentProductRepository.loadSecondLatest() ?: return
        _recentProduct.value = recentProduct
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
        cartRepository.addCartItem(productId, quantity) { result ->
            result.onSuccess {
                _isAddCartSuccess.postValue(Event(true))
            }.onFailure {
                _isAddCartSuccess.postValue(Event(false))
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    override fun onRecentProductClicked(productId: Long) {
        _navigateToDetail.postValue(Event(productId))
    }

    override fun onBackButtonClicked() {
        _moveBack.postValue(Event(true))
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
}
