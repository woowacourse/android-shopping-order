package woowacourse.shopping.view.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.data.model.CartItemEntity.Companion.DEFAULT_CART_ITEM_COUNT
import woowacourse.shopping.data.repository.ProductRepositoryImpl.Companion.DEFAULT_ITEM_SIZE
import woowacourse.shopping.domain.model.CartItemCounter.Companion.DEFAULT_ITEM_COUNT
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.BaseViewModel
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyProductRepository: RecentlyProductRepository,
) : BaseViewModel(), OnClickCartItemCounter {
    private val _products: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val products: LiveData<List<Product>> get() = _products
    private val _cartItemCount: MutableLiveData<Int> = MutableLiveData(0)
    val cartItemCount: LiveData<Int> get() = _cartItemCount

    private val _recentlyProducts: MutableLiveData<List<RecentlyProduct>> =
        MutableLiveData(emptyList())
    val recentlyProducts: LiveData<List<RecentlyProduct>> get() = _recentlyProducts

    private val _productListEvent: MutableSingleLiveData<ProductListEvent> =
        MutableSingleLiveData()
    val productListEvent: SingleLiveData<ProductListEvent> get() = _productListEvent

    init {
        updateTotalCartItemCount()
        loadPagingRecentlyProduct()
    }

    fun loadPagingProduct() {
        val itemSize = products.value?.size ?: DEFAULT_ITEM_SIZE
        productRepository.loadPagingProducts(itemSize)
            .onSuccess {
                _products.value = _products.value?.plus(it)
            }
            .onFailure {
                handleException(ErrorEvent.LoadDataEvent())
            }
    }

    fun loadPagingRecentlyProduct() {
        recentlyProductRepository.getRecentlyProductList()
            .onSuccess {
                _recentlyProducts.value = it
            }
            .onFailure {
                handleException(ErrorEvent.LoadDataEvent())
            }
    }

    private fun updateCarItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ) {
        shoppingCartRepository.updateCartItem(
            product,
            updateCartItemType,
        )
            .onSuccess { updateCartItemResult ->
                when (updateCartItemResult) {
                    UpdateCartItemResult.ADD -> addCartItem(product)
                    is UpdateCartItemResult.DELETE -> deleteCartItem(product)
                    is UpdateCartItemResult.UPDATED -> updateCartItem(
                        product = product,
                        itemCount = updateCartItemResult.cartItemResult.counter.itemCount,
                        updateCartItemType = updateCartItemType,
                    )
                }
            }.onFailure {
                handleException(ErrorEvent.UpdateCartEvent())
            }
    }

    private fun updateCartItem(
        product: Product,
        itemCount: Int,
        updateCartItemType: UpdateCartItemType,
    ) {
        product.updateCartItemCount(itemCount)
        when (updateCartItemType) {
            UpdateCartItemType.DECREASE -> {
                updateTotalCartItemCount()
            }

            UpdateCartItemType.INCREASE -> {
                product.updateItemSelector(true)
                updateTotalCartItemCount()
            }

            is UpdateCartItemType.UPDATE -> {}
        }
        _productListEvent.setValue(ProductListEvent.UpdateProductEvent.Success(product.id))
    }

    private fun addCartItem(product: Product) {
        product.updateCartItemCount(DEFAULT_CART_ITEM_COUNT)
        product.updateItemSelector(true)
        updateTotalCartItemCount()
        _productListEvent.setValue(ProductListEvent.UpdateProductEvent.Success(product.id))
    }

    private fun deleteCartItem(product: Product) {
        product.updateItemSelector(false)
        updateTotalCartItemCount()
        _productListEvent.setValue(ProductListEvent.DeleteProductEvent.Success(product.id))
    }

    private fun updateTotalCartItemCount() {
        shoppingCartRepository.getTotalCartItemCount()
            .onSuccess {
                _cartItemCount.value = it
            }
            .onFailure {
                handleException(ErrorEvent.LoadDataEvent())
            }
    }

    fun updateProducts(items: Map<Long, Int>) {
        products.value?.forEach { product ->
            val count = items[product.id]
            if (count != null) {
                product.updateItemSelector(count != DEFAULT_ITEM_COUNT)
                product.updateCartItemCount(count)
                _productListEvent.setValue(ProductListEvent.UpdateProductEvent.Success(product.id))
            }
        }
        updateTotalCartItemCount()
    }

    override fun clickIncrease(product: Product) {
        updateCarItem(product, UpdateCartItemType.INCREASE)
    }

    override fun clickDecrease(product: Product) {
        updateCarItem(product, UpdateCartItemType.DECREASE)
    }
}
