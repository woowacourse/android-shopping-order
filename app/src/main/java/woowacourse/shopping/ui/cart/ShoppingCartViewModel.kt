package woowacourse.shopping.ui.cart

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.MutableSingleLiveData
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.SingleLiveData
import woowacourse.shopping.UniversalViewModelFactory
import woowacourse.shopping.domain.repository.DefaultShoppingProductRepository
import woowacourse.shopping.domain.repository.ShoppingProductsRepository
import woowacourse.shopping.remote.CartItemDto
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener
import kotlin.concurrent.thread

class ShoppingCartViewModel(
    private val shoppingProductsRepository: ShoppingProductsRepository,
) : ViewModel(), OnProductItemClickListener, OnItemQuantityChangeListener, OnCartItemSelectedListener {
    private val uiHandler = Handler(Looper.getMainLooper())

    private var _itemsInCurrentPage = MutableLiveData<List<CartItemDto>>()
    val itemsInCurrentPage: LiveData<List<CartItemDto>> get() = _itemsInCurrentPage

    private var _deletedItemId: MutableSingleLiveData<Long> = MutableSingleLiveData()
    val deletedItemId: SingleLiveData<Long> get() = _deletedItemId

    private var _selectedProducts = MutableLiveData<List<Long>>(emptyList())
    val selectedProducts: LiveData<List<Long>> get() = _selectedProducts

    fun loadAll() {
        thread {
            val currentItems =
                shoppingProductsRepository.loadPagedCartItem()

            uiHandler.post {
                _itemsInCurrentPage.value = currentItems
            }
        }
    }

    fun deleteItem(cartItemId: Long) {
        thread {
            shoppingProductsRepository.removeShoppingCartProduct(cartItemId)
            val currentItems =
                shoppingProductsRepository.loadPagedCartItem()

            uiHandler.post {
                _itemsInCurrentPage.value = currentItems
            }
        }
    }

    override fun onClick(productId: Long) {
        _deletedItemId.setValue(productId)
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        thread {
            shoppingProductsRepository.increaseShoppingCartProduct(productId, quantity)
            val currentItems =
                shoppingProductsRepository.loadPagedCartItem()
            uiHandler.post {
                _itemsInCurrentPage.value = currentItems
            }
        }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        thread {
            shoppingProductsRepository.decreaseShoppingCartProduct(productId, quantity)
            val currentItems =
                shoppingProductsRepository.loadPagedCartItem()
            uiHandler.post {
                _itemsInCurrentPage.value = currentItems
            }
        }
    }

    override fun selected(cartItemId: Long) {
        val cartItemIds = selectedProducts.value ?: emptyList()
        if (!cartItemIds.contains(cartItemId)) {
            _selectedProducts.value = cartItemIds + cartItemId
        } else {
            _selectedProducts.value = cartItemIds - cartItemId
        }

        Log.d("cart", "cart id : $cartItemId")
        Log.d("cart", "selected : ${selectedProducts.value}")
    }

    companion object {
        private const val TAG = "ShoppingCartViewModel"

        fun factory(
            shoppingProductsRepository: ShoppingProductsRepository =
                DefaultShoppingProductRepository(
                    productsSource = ShoppingApp.productSource,
                    cartSource = ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ShoppingCartViewModel(shoppingProductsRepository)
            }
    }
}
