package woowacourse.shopping.presentation.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class DetailViewModel(
    private val cartRepository: CartRepository,
    private val shoppingRepository: ShoppingItemsRepository,
    private val recentProductRepository: RecentProductRepository,
    val productId: Long,
) : ViewModel(), DetailEventHandler, CounterHandler {
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

    private val _addCartItem = MutableLiveData<Event<Long>>()
    val addCartItem: LiveData<Event<Long>>
        get() = _addCartItem

    init {
        Log.d("crong", "DetailViewModel: init")
        Log.d("crong", "DetailViewModel: productId $productId")
        viewModelScope.launch {
            loadShoppingProductData()
            loadRecentProductData()
            checkRecentProductVisibility()
        }
    }

    private suspend fun loadShoppingProductData() {
        val loadedData =
            viewModelScope.async {
                shoppingRepository.findProductItem(productId)
            }
        // Log.d("crong", "loadShoppingProductData: ${loadedData.await()}")
        loadedData.await().onSuccess {
            Log.d("crong", "loadShoppingProductData1: $it")
            _shoppingProduct.value =
                ShoppingProduct(
                    product = it ?: return@onSuccess,
                    quantity = fetchQuantity(),
                )
        }
        Log.d("crong", "loadShoppingProductData2: ${_shoppingProduct.value}")

        /*viewModelScope.launch {
            val product = shoppingRepository.findProductItem(productId)
            product.onSuccess {
                val shoppingProduct =
                    ShoppingProduct(
                        product = it ?: return@onSuccess,
                        quantity = fetchQuantity(),
                    )
                _shoppingProduct.value = shoppingProduct
            }
        }*/
    }

    private suspend fun fetchQuantity(): Int {
        var quantity = 1
        val transaction =
            viewModelScope.async {
                cartRepository.findOrNullWithProductId(productId)
            }
        transaction.await().onSuccess {
            quantity = it?.quantity ?: 1
        }
        Log.d("crong", "fetchQuantity: $quantity")
        return quantity
    }

    private fun loadRecentProductData() {
        viewModelScope.launch {
            val recentProduct = recentProductRepository.loadSecondLatest()
            Log.d("crong", "loadRecentProductData: $recentProduct")
            recentProduct.onSuccess {
                _recentProduct.value = it
                checkRecentProductVisibility()
            }
        }
    }

    fun createShoppingCartItem() {
        val product = shoppingProduct.value?.product ?: return
        val quantity = shoppingProduct.value?.quantity() ?: return
        viewModelScope.launch {
            cartRepository.insert(product = product, quantity = quantity)
        }
        // cartRepository.insert(product = product, quantity = quantity)
    }

    private fun checkRecentProductVisibility() {
        _recentProductVisibility.value =
            !(recentProduct.value == null || recentProduct.value?.productId == productId)
    }

    override fun addCartItem(productId: Long) {
        _addCartItem.postValue(Event(productId))
    }

    override fun moveBack() {
        _moveBack.postValue(Event(true))
    }

    override fun onRecentProductClick(productId: Long) {
        _navigateToDetail.postValue(Event(productId))
    }

    override fun increaseCount(productId: Long) {
        _shoppingProduct.value?.increase()
        _shoppingProduct.value = _shoppingProduct.value
    }

    override fun decreaseCount(productId: Long) {
        _shoppingProduct.value?.decrease()
        _shoppingProduct.value = _shoppingProduct.value
    }
}
