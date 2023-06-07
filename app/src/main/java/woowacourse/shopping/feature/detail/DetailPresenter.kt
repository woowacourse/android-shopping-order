package woowacourse.shopping.feature.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.BaseResponse
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel

class DetailPresenter(
    val view: DetailContract.View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val productId: Long,
    private val recentProductUiModel: RecentProductUiModel?,
) : DetailContract.Presenter {
    private val _product: MutableLiveData<ProductUiModel> =
        MutableLiveData(ProductUiModel(-1, "", "", 0, 0))
    override val product: LiveData<ProductUiModel>
        get() = _product

    private val isRecentProduct: Boolean
        get() {
            recentProductUiModel?.let {
                return it.product.id == productId
            }
            return false
        }

    override fun initPresenter() {
        productRepository.fetchProductById(productId) { result ->
            when (result) {
                is BaseResponse.SUCCESS -> findCartInfoByProduct(result.response)
                is BaseResponse.FAILED -> failedLoadDetailData()
                is BaseResponse.NETWORK_ERROR -> {}
            }
        }

        if (isRecentProduct || recentProductUiModel == null) {
            view.hideRecentScreen()
        } else {
            val recentProduct = recentProductUiModel.product
            view.setRecentScreen(recentProduct.name, recentProduct.toMoneyFormat())
        }
    }

    private fun findCartInfoByProduct(product: Product) {
        cartRepository.fetchAll { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    val cartProducts = result.response
                    val productUiModel =
                        cartProducts.find { it.product.id == productId }
                            ?.toPresentation()?.productUiModel

                    productUiModel?.let {
                        _product.postValue(it)
                    } ?: _product.postValue(product.toPresentation())
                }
                is BaseResponse.FAILED -> view.showFailedLoadProductInfo()
                is BaseResponse.NETWORK_ERROR -> view.showNetworkError()
            }
        }
    }

    override fun updateProductCount(count: Int) {
        if (count < 0) return
        _product.value?.count = count
    }

    override fun handleAddCartClick() {
        _product.value?.let {
            cartRepository.fetchAll { result ->
                when (result) {
                    is BaseResponse.SUCCESS -> {
                        val carts = result.response
                        val cartProductId =
                            carts.find { it.product.id == productId }?.cartId
                        view.showSelectCartProductCountScreen(it, cartProductId)
                    }
                    is BaseResponse.FAILED -> view.showFailedLoadProductInfo()
                    is BaseResponse.NETWORK_ERROR -> view.showNetworkError()
                }
            }
        }
    }

    override fun navigateRecentProductDetail() {
        recentProductUiModel?.let { recentProductUiModel ->
            recentProductRepository.addRecentProduct(recentProductUiModel.product.toDomain()) { result ->
                when (result) {
                    is BaseResponse.SUCCESS -> view.showRecentProductDetailScreen(
                        recentProductUiModel
                    )
                    else -> view.showRetryMessage()
                }
            }
        }
    }

    override fun setProductCountInfo(count: Int) {
        _product.value?.count = count
    }

    private fun failedLoadDetailData() {
        view.showFailedLoadProductInfo()
        view.exitDetailScreen()
    }

    override fun exit() {
        view.exitDetailScreen()
    }
}
