package woowacourse.shopping.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
        checkValidProduct(product)
        when (cartItemId) {
            DEFAULT_CART_ITEM_ID -> shoppingCartRepository.addCartItem(product)
            else ->
                shoppingCartRepository.updateCartItem(
                    product = product,
                    updateCartItemType = UpdateCartItemType.UPDATE(product.cartItemCounter.itemCount),
                )
        }.onSuccess {
            _productDetailEvent.setValue(
                ProductDetailEvent.AddShoppingCart.Success(
                    productId = product.id,
                    count = product.cartItemCounter.itemCount,
                ),
            )
        }.onFailure { e ->
            _errorEvent.setValue(
                when (e) {
                    is NoSuchDataException -> ProductDetailEvent.AddShoppingCart.Fail
                    else -> ProductDetailEvent.ErrorEvent.NotKnownError
                },
            )
        }
    }

    fun loadProductItem(productId: Long) {
        loadProductItemCount(productId).onSuccess { loadItemCounter ->
            productRepository.getProduct(productId).onSuccess { product ->
                product.updateItemSelector(true)
                product.updateCartItemCount(loadItemCounter.itemCount)
                loadRecentlyProduct(product)
                _product.value = product
            }.onFailure { e ->
                _errorEvent.setValue(
                    when (e) {
                        is NoSuchDataException -> ProductDetailEvent.LoadProductItem.Fail
                        else -> ProductDetailEvent.ErrorEvent.NotKnownError
                    },
                )
            }
        }.onFailure { e ->
            _errorEvent.setValue(
                when (e) {
                    is NoSuchDataException -> ProductDetailEvent.LoadProductItem.Fail
                    else -> ProductDetailEvent.ErrorEvent.NotKnownError
                },
            )
        }
    }

    private fun loadProductItemCount(productId: Long): Result<CartItemCounter> {
        return shoppingCartRepository.getCartItemResultFromProductId(productId).mapCatching { result ->
            cartItemId = result.cartItemId
            result.counter
        }.recover {
            _errorEvent.setValue(ProductDetailEvent.ErrorEvent.NotKnownError)
            CartItemCounter()
        }
    }

    fun increaseItemCounter() {
        product.value?.cartItemCounter?.increase()
        _product.value =
            product.value?.let {
                it.copy(cartItemCounter = it.cartItemCounter)
            }
    }

    fun decreaseItemCounter() {
        val productCount = product.value?.cartItemCounter?.itemCount ?: DEFAULT_ITEM_COUNT
        if (productCount > DEFAULT_ITEM_COUNT) {
            product.value?.cartItemCounter?.decrease()
            _product.value =
                product.value?.let {
                    it.copy(cartItemCounter = it.cartItemCounter)
                }
        }
    }

    private fun saveRecentlyProduct(product: Product) {
        recentlyProductRepository.addRecentlyProduct(
            RecentlyProduct(
                productId = product.id,
                imageUrl = product.imageUrl,
                name = product.name,
                category = product.category,
            ),
        )
    }

    private fun deletePrevRecentlyProduct(recentlyProductId: Long): Result<Unit> {
        return recentlyProductRepository.deleteRecentlyProduct(recentlyProductId)
    }

    fun updateRecentlyProduct(recentlyProduct: RecentlyProduct) {
        deletePrevRecentlyProduct(recentlyProduct.id).onSuccess {
            loadProductItemCount(recentlyProduct.productId).onSuccess { loadItemCounter ->
                productRepository.getProduct(recentlyProduct.productId).onSuccess { product ->
                    product.updateItemSelector(true)
                    product.updateCartItemCount(loadItemCounter.itemCount)
                    _product.value = product
                }.onFailure { e ->
                    _errorEvent.setValue(
                        when (e) {
                            is NoSuchDataException -> ProductDetailEvent.UpdateRecentlyProductItem.Fail
                            else -> ProductDetailEvent.ErrorEvent.NotKnownError
                        },
                    )
                }
            }
            _recentlyProduct.value = RecentlyProduct.defaultRecentlyProduct
            _productDetailEvent.setValue(ProductDetailEvent.UpdateRecentlyProductItem.Success)
        }.onFailure { e ->
            _errorEvent.setValue(
                when (e) {
                    is NoSuchDataException -> ProductDetailEvent.UpdateRecentlyProductItem.Fail
                    else -> ProductDetailEvent.ErrorEvent.NotKnownError
                },
            )
        }
    }

    private fun loadRecentlyProduct(product: Product) {
        recentlyProductRepository.getMostRecentlyProduct().onSuccess { recentlyProduct ->
            _recentlyProduct.value = recentlyProduct
            _productDetailEvent.setValue(ProductDetailEvent.UpdateRecentlyProductItem.Success)
            if (recentlyProduct.productId != product.id) {
                saveRecentlyProduct(product)
            }
        }.onFailure { e ->
            _errorEvent.setValue(
                when (e) {
                    is NoSuchDataException -> ProductDetailEvent.UpdateRecentlyProductItem.Fail
                    else -> ProductDetailEvent.ErrorEvent.NotKnownError
                },
            )
        }
    }

    private fun checkValidProduct(product: Product) {
        if (product.id == DEFAULT_PRODUCT_ID) throw NoSuchDataException()
        if (product.cartItemCounter.itemCount == DEFAULT_ITEM_COUNT) throw NoSuchDataException()
    }
}
