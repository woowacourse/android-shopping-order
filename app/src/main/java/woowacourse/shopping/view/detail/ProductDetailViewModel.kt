package woowacourse.shopping.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.cart.CartItem.Companion.DEFAULT_CART_ITEM_ID
import woowacourse.shopping.domain.model.cart.CartItemCounter
import woowacourse.shopping.domain.model.cart.CartItemCounter.Companion.DEFAULT_ITEM_COUNT
import woowacourse.shopping.domain.model.cart.UpdateCartItemType
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.Product.Companion.DEFAULT_PRODUCT_ID
import woowacourse.shopping.domain.model.product.RecentlyProduct
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.BaseViewModel
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyProductRepository: RecentlyProductRepository,
) : BaseViewModel(), OnClickCartItemCounter, OnClickDetail {
    private val _product: MutableLiveData<Product> = MutableLiveData(Product.defaultProduct)
    val product: LiveData<Product> get() = _product
    private var cartItemId: Long = DEFAULT_CART_ITEM_ID

    private val _recentlyProduct: MutableLiveData<RecentlyProduct> =
        MutableLiveData(RecentlyProduct.defaultRecentlyProduct)
    val recentlyProduct: LiveData<RecentlyProduct> get() = _recentlyProduct

    private val _productDetailEvent = MutableSingleLiveData<ProductDetailEvent>()
    val productDetailEvent: SingleLiveData<ProductDetailEvent> = _productDetailEvent

    fun addShoppingCartItem(product: Product) =
        viewModelScope.launch {
            if (isInValidProduct(product)) {
                handleException(ErrorEvent.AddCartEvent())
                return@launch
            }
            when (cartItemId) {
                DEFAULT_CART_ITEM_ID -> {
                    shoppingCartRepository.addCartItem(product)
                        .onSuccess {
                            _productDetailEvent.setValue(
                                ProductDetailEvent.AddShoppingCart.Success(
                                    productId = product.id,
                                    count = product.cartItemCounter.itemCount,
                                ),
                            )
                        }
                        .onFailure {
                            handleException(ErrorEvent.AddCartEvent())
                        }
                }

                else -> {
                    shoppingCartRepository.updateCartItem(
                        product = product,
                        UpdateCartItemType.UPDATE(product.cartItemCounter.itemCount),
                    )
                        .onSuccess {
                            _productDetailEvent.setValue(
                                ProductDetailEvent.AddShoppingCart.Success(
                                    productId = product.id,
                                    count = product.cartItemCounter.itemCount,
                                ),
                            )
                        }
                        .onFailure {
                            handleException(ErrorEvent.UpdateCartEvent())
                        }
                }
            }
        }

    fun loadProductItem(productId: Long) =
        viewModelScope.launch {
            loadProductItemCount(productId)
                .onSuccess { loadItemCounter ->
                    updateLoadProduct(productId, loadItemCounter)
                }
                .onFailure {
                    handleException(ErrorEvent.LoadDataEvent())
                }
        }

    private fun updateLoadProduct(
        productId: Long,
        loadItemCounter: CartItemCounter,
    ) = viewModelScope.launch {
        productRepository.getProduct(productId)
            .onSuccess { product ->
                product.updateItemSelector(true)
                product.updateCartItemCount(loadItemCounter.itemCount)
                loadRecentlyProduct(product)
                _product.value = product
            }
            .onFailure {
                handleException(ErrorEvent.LoadDataEvent())
            }
    }

    private suspend fun loadProductItemCount(productId: Long): Result<CartItemCounter> {
        return runCatching {
            shoppingCartRepository.getCartItemResultFromProductId(productId = productId)
                .mapCatching { result ->
                    cartItemId = result.cartItemId
                    result.counter
                }.getOrDefault(CartItemCounter())
        }
    }

    private fun increaseItemCounter() {
        product.value?.cartItemCounter?.increase()
        _product.value =
            product.value?.cartItemCounter?.let {
                product.value?.copy(
                    cartItemCounter = it,
                )
            }
    }

    private fun decreaseItemCounter() {
        val productCount = product.value?.cartItemCounter?.itemCount ?: DEFAULT_ITEM_COUNT
        if (productCount > DEFAULT_ITEM_COUNT) {
            product.value?.cartItemCounter?.decrease()
            _product.value =
                product.value?.cartItemCounter?.let {
                    product.value?.copy(
                        cartItemCounter = it,
                    )
                }
        }
    }

    private fun saveRecentlyProduct(product: Product) =
        viewModelScope.launch {
            recentlyProductRepository.addRecentlyProduct(
                RecentlyProduct(
                    productId = product.id,
                    imageUrl = product.imageUrl,
                    name = product.name,
                    category = product.category,
                ),
            )
        }

    private fun deletePrevRecentlyProduct(recentlyProductId: Long) =
        viewModelScope.launch {
            recentlyProductRepository.deleteRecentlyProduct(recentlyProductId)
        }

    private fun updateRecentlyProduct(recentlyProduct: RecentlyProduct) =
        viewModelScope.launch {
            deletePrevRecentlyProduct(recentlyProduct.id)
            productRepository.getProduct(recentlyProduct.productId)
                .onSuccess { product ->
                    updateLoadRecentlyProduct(recentlyProduct, product)
                }
                .onFailure {
                    handleException(ErrorEvent.LoadDataEvent())
                }
        }

    private fun updateLoadRecentlyProduct(
        recentlyProduct: RecentlyProduct,
        product: Product,
    ) = viewModelScope.launch {
        loadProductItemCount(recentlyProduct.productId)
            .onSuccess { loadItemCounter ->
                product.updateItemSelector(true)
                product.updateCartItemCount(loadItemCounter.itemCount)
                _product.value = product
                _recentlyProduct.value = RecentlyProduct.defaultRecentlyProduct
                _productDetailEvent.setValue(ProductDetailEvent.UpdateRecentlyProductItem.Success)
            }
            .onFailure {
                handleException(ErrorEvent.UpdateCartEvent())
            }
    }

    private fun loadRecentlyProduct(product: Product) =
        viewModelScope.launch {
            recentlyProductRepository.getMostRecentlyProduct()
                .onSuccess { recentlyProduct ->
                    _recentlyProduct.value = recentlyProduct
                    _productDetailEvent.setValue(ProductDetailEvent.UpdateRecentlyProductItem.Success)
                    if (recentlyProduct.productId != product.id) {
                        saveRecentlyProduct(product)
                    }
                }
                .onFailure {
                    saveRecentlyProduct(product)
                }
        }

    private fun isInValidProduct(product: Product): Boolean {
        return product.id == DEFAULT_PRODUCT_ID || product.cartItemCounter.itemCount == DEFAULT_ITEM_COUNT
    }

    override fun clickIncrease(product: Product) {
        increaseItemCounter()
    }

    override fun clickDecrease(product: Product) {
        decreaseItemCounter()
    }

    override fun clickAddCart(product: Product) {
        addShoppingCartItem(product)
    }

    override fun clickRecently(recentlyProduct: RecentlyProduct) {
        updateRecentlyProduct(recentlyProduct)
    }
}
