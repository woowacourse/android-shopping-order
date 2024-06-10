package woowacourse.shopping.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItem.Companion.DEFAULT_CART_ITEM_ID
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemCounter.Companion.DEFAULT_ITEM_COUNT
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.DEFAULT_PRODUCT_ID
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyProductRepository: RecentlyProductRepository,
) : ViewModel() {
    private val _product: MutableLiveData<Product> = MutableLiveData(Product.defaultProduct)
    val product: LiveData<Product> get() = _product
    private var cartItemId: Long = DEFAULT_CART_ITEM_ID

    private val _recentlyProduct: MutableLiveData<RecentlyProduct> =
        MutableLiveData(RecentlyProduct.defaultRecentlyProduct)
    val recentlyProduct: LiveData<RecentlyProduct> get() = _recentlyProduct

    private val _errorEvent: MutableSingleLiveData<ProductDetailEvent.ErrorEvent> =
        MutableSingleLiveData()
    val errorEvent: SingleLiveData<ProductDetailEvent.ErrorEvent> get() = _errorEvent

    private val _productDetailEvent = MutableSingleLiveData<ProductDetailEvent.SuccessEvent>()
    val productDetailEvent: SingleLiveData<ProductDetailEvent.SuccessEvent> = _productDetailEvent

    fun addShoppingCartItem(product: Product) {
        viewModelScope.launch {
            try {
                checkValidProduct(product)
                when (cartItemId) {
                    DEFAULT_CART_ITEM_ID -> {
                        shoppingCartRepository.addCartItem(product)
                    }

                    else -> {
                        shoppingCartRepository.updateCartItem(
                            product = product,
                            UpdateCartItemType.UPDATE(product.cartItemCounter.itemCount),
                        )
                    }
                }
                _productDetailEvent.setValue(
                    ProductDetailEvent.AddShoppingCart.Success(
                        productId = product.id,
                        count = product.cartItemCounter.itemCount,
                    ),
                )
            } catch (e: Exception) {
                when (e) {
                    is NoSuchDataException ->
                        _errorEvent.setValue(
                            ProductDetailEvent.AddShoppingCart.Fail,
                        )

                    else ->
                        _errorEvent.setValue(
                            ProductDetailEvent.ErrorEvent.NotKnownError,
                        )
                }
            }
        }
    }

    fun loadProductItem(productId: Long) =
        viewModelScope.launch {
            val loadItemCounter =
                loadProductItemCount(productId)
                    .onSuccess {
                        val product = productRepository.getProduct(productId).getOrThrow()
                        product.updateItemSelector(true)
                        product.updateCartItemCount(it.itemCount)
                        loadRecentlyProduct(product)
                        _product.value = product
                    }
                    .onFailure {
                        when (it) {
                            is NoSuchDataException ->
                                _errorEvent.setValue(
                                    ProductDetailEvent.LoadProductItem.Fail,
                                )

                            else ->
                                _errorEvent.setValue(
                                    ProductDetailEvent.ErrorEvent.NotKnownError,
                                )
                        }
                    }
        }

    fun updateRecentlyProduct(recentlyProduct: RecentlyProduct) =
        viewModelScope.launch {
            deletePrevRecentlyProduct(recentlyProduct.id)
            loadProductItemCount(recentlyProduct.productId)
                .onSuccess { loadItemCounter ->
                    val product = productRepository.getProduct(recentlyProduct.productId).getOrThrow()
                    product.updateItemSelector(true)
                    product.updateCartItemCount(loadItemCounter.itemCount)
                    _product.value = product
                    _recentlyProduct.value = RecentlyProduct.defaultRecentlyProduct
                    _productDetailEvent.setValue(ProductDetailEvent.UpdateRecentlyProductItem.Success)
                }
                .onFailure {
                    when (it) {
                        is NoSuchDataException ->
                            _errorEvent.setValue(
                                ProductDetailEvent.LoadProductItem.Fail,
                            )

                        else ->
                            _errorEvent.setValue(
                                ProductDetailEvent.ErrorEvent.NotKnownError,
                            )
                    }
                }
        }

    fun increaseItemCounter() {
        product.value?.cartItemCounter?.increase()
        _product.value =
            product.value?.cartItemCounter?.let {
                product.value?.copy(
                    cartItemCounter = it,
                )
            }
    }

    fun decreaseItemCounter() {
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

    private suspend fun loadProductItemCount(productId: Long): Result<CartItemCounter> {
        return shoppingCartRepository.getCartItemResultFromProductId(productId = productId)
            .mapCatching { result ->
                cartItemId = result.cartItemId
                result.counter
            }
            .recoverCatching {
                CartItemCounter()
            }
    }

    private fun deletePrevRecentlyProduct(recentlyProductId: Long) =
        viewModelScope.launch {
            recentlyProductRepository.deleteRecentlyProduct(recentlyProductId)
        }

    private fun loadRecentlyProduct(product: Product) =
        viewModelScope.launch {
            try {
                val recentlyProduct = recentlyProductRepository.getMostRecentlyProduct().getOrThrow()
                _recentlyProduct.value = recentlyProduct
                _productDetailEvent.setValue(ProductDetailEvent.UpdateRecentlyProductItem.Success)
                if (recentlyProduct.productId != product.id) {
                    saveRecentlyProduct(product)
                }
            } catch (e: Exception) {
                when (e) {
                    is NoSuchDataException -> _errorEvent.setValue(ProductDetailEvent.UpdateRecentlyProductItem.Fail)
                    else -> _errorEvent.setValue(ProductDetailEvent.ErrorEvent.NotKnownError)
                }
            }
        }

    private fun checkValidProduct(product: Product) {
        if (product.id == DEFAULT_PRODUCT_ID) throw NoSuchDataException()
        if (product.cartItemCounter.itemCount == DEFAULT_ITEM_COUNT) throw NoSuchDataException()
    }
}
