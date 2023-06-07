package woowacourse.shopping.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.BaseResponse
import com.example.domain.model.CartProduct
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.feature.main.MainContract.View.MainScreenEvent
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel

class MainPresenter(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : MainContract.Presenter {
    private val _products: MutableLiveData<List<CartProductUiModel>> = MutableLiveData()
    override val products: LiveData<List<CartProductUiModel>>
        get() = _products

    private val _recentProducts: MutableLiveData<List<RecentProductUiModel>> = MutableLiveData()
    override val recentProducts: LiveData<List<RecentProductUiModel>>
        get() = _recentProducts

    private val _badgeCount: MutableLiveData<Int> = MutableLiveData()
    override val badgeCount: LiveData<Int>
        get() = _badgeCount

    private val _mainScreenEvent: MutableLiveData<MainScreenEvent> = MutableLiveData()
    override val mainScreenEvent: LiveData<MainScreenEvent>
        get() = _mainScreenEvent

    override fun initLoadProducts() {
        _mainScreenEvent.value = MainScreenEvent.ShowLoading
        productRepository.fetchFirstProducts { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    loadCartInfo(result.response)
                }
                is BaseResponse.FAILED -> showFailedLoadProduct()
                is BaseResponse.NETWORK_ERROR -> showNetworkError()
            }
        }
    }

    override fun loadMoreProducts() {
        val lastProductId = _products.value?.lastOrNull()?.productUiModel?.id ?: 0
        productRepository.fetchNextProducts(lastProductId) { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    val alreadyProducts =
                        _products.value?.map { it.productUiModel.toDomain() } ?: emptyList()
                    val nextProducts = result.response
                    loadCartInfo(alreadyProducts + nextProducts)
                }
                is BaseResponse.FAILED -> {
                    _mainScreenEvent.postValue(MainScreenEvent.HideLoadMore)
                }
                is BaseResponse.NETWORK_ERROR -> showNetworkError()
            }
        }
    }

    private fun loadCartInfo(products: List<Product>) {
        cartRepository.fetchAll { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    val cartsInfo = result.response
                    val cartProductUiModels = createCartProductUiModels(products, cartsInfo)
                    _products.postValue(cartProductUiModels)
                    _mainScreenEvent.postValue(MainScreenEvent.HideLoading)
                    updateCartCountBadge()
                }
                is BaseResponse.FAILED -> showFailedLoadCartInfo()
                is BaseResponse.NETWORK_ERROR -> showNetworkError()
            }
        }
    }

    override fun loadRecentProducts() {
        recentProductRepository.fetchAllRecentProduct { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    val recentProducts = result.response
                    val recentProductUiModels = recentProducts.map { it.toPresentation() }
                    _recentProducts.postValue(recentProductUiModels)
                }
                else -> showRetryMessage()
            }
        }
    }

    override fun showCartCount() {
        updateCartCountBadge()
    }

    override fun showProductDetail(productId: Long) {
        productRepository.fetchProductById(productId) { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    val productUiModel = result.response.toPresentation()
                    val recentProductUiModel = _recentProducts.value?.firstOrNull()
                    navigateDetailScreen(productUiModel, recentProductUiModel)
                }
                is BaseResponse.FAILED -> showFailedLoadProduct()
                is BaseResponse.NETWORK_ERROR -> showNetworkError()
            }
        }
    }

    private fun navigateDetailScreen(
        productUiModel: ProductUiModel,
        recentProductUiModel: RecentProductUiModel?
    ) {
        recentProductRepository.addRecentProduct(productUiModel.toDomain()) { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    _mainScreenEvent.postValue(
                        MainScreenEvent.ShowProductDetailScreen(
                            productUiModel,
                            recentProductUiModel
                        )
                    )
                }
                else -> showRetryMessage()
            }
        }
    }

    override fun changeProductCartCount(productId: Long, count: Int) {
        val cartProductUiModel: CartProductUiModel =
            _products.value?.find { it.productUiModel.id == productId }
                ?: return
        when {
            cartProductUiModel.cartId <= 0 -> addFirstProductToCart(cartProductUiModel)
            count == 0 -> deleteCartProduct(cartProductUiModel)
            else -> updateCartProductCount(cartProductUiModel, count)
        }
    }

    private fun addFirstProductToCart(cartProductUiModel: CartProductUiModel) {
        val productId = cartProductUiModel.productUiModel.id
        cartRepository.addCartProduct(productId) { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    val cartId = result.response
                    cartProductUiModel.cartId = cartId
                    cartProductUiModel.productUiModel.count = 1
                    updateCartCountBadge()
                }
                is BaseResponse.FAILED -> resetProductsStateForFail(productId)
                is BaseResponse.NETWORK_ERROR -> resetProductsStateForNetwork(productId)
            }
        }
    }

    private fun deleteCartProduct(cartProductUiModel: CartProductUiModel) {
        val cartId = cartProductUiModel.cartId
        val productId = cartProductUiModel.productUiModel.id
        cartRepository.deleteCartProduct(cartId) { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    cartProductUiModel.cartId = -1
                    cartProductUiModel.productUiModel.count = 0
                    updateCartCountBadge()
                }
                is BaseResponse.FAILED -> resetProductsStateForFail(productId)
                is BaseResponse.NETWORK_ERROR -> resetProductsStateForNetwork(productId)
            }
        }
    }

    private fun updateCartProductCount(cartProductUiModel: CartProductUiModel, count: Int) {
        val cartId = cartProductUiModel.cartId
        val productId = cartProductUiModel.productUiModel.id
        cartRepository.changeCartProductCount(cartId, count) { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    cartProductUiModel.productUiModel.count = count
                    updateCartCountBadge()
                }
                is BaseResponse.FAILED -> resetProductsStateForFail(productId)
                is BaseResponse.NETWORK_ERROR -> resetProductsStateForNetwork(productId)
            }
        }
    }

    private fun updateCartCountBadge() {
        cartRepository.fetchSize { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    val size = result.response
                    _badgeCount.postValue(size)
                }
                is BaseResponse.FAILED -> {}
                is BaseResponse.NETWORK_ERROR -> showNetworkError()
            }
        }
    }

    private fun resetProductsStateForFail(productId: Long) {
        _products.value = _products.value?.toList()
        _mainScreenEvent.value = MainScreenEvent.ReBindProductItem(productId)
        showFailedChangeCartCount()
    }

    private fun resetProductsStateForNetwork(productId: Long) {
        _products.value = _products.value?.toList()
        _mainScreenEvent.value = MainScreenEvent.ReBindProductItem(productId)
        showNetworkError()
    }

    override fun moveToCart() {
        _mainScreenEvent.value = MainScreenEvent.ShowCartScreen
    }

    override fun moveToOrderList() {
        _mainScreenEvent.value = MainScreenEvent.ShowOrderListScreen
    }

    private fun showFailedLoadProduct() {
        _mainScreenEvent.value = MainScreenEvent.ShowFailedLoadProduct
    }

    private fun showFailedLoadCartInfo() {
        _mainScreenEvent.value = MainScreenEvent.ShowFailedLoadCartInfo
    }

    private fun showFailedChangeCartCount() {
        _mainScreenEvent.value = MainScreenEvent.ShowFailedChangeCartCount
    }

    private fun showNetworkError() {
        _mainScreenEvent.value = MainScreenEvent.ShowNetworkError
    }

    private fun showRetryMessage() {
        _mainScreenEvent.value = MainScreenEvent.ShowRetryMessage
    }

    private fun createCartProductUiModels(
        products: List<Product>,
        cartInfo: List<CartProduct>
    ): List<CartProductUiModel> {
        val cartItems = cartInfo.associateBy { it.product.id }
        return products.map { product ->
            val cartItem = cartItems[product.id]
            cartItem?.toPresentation() ?: CartProductUiModel(-1, product.toPresentation(), false)
        }
    }
}
