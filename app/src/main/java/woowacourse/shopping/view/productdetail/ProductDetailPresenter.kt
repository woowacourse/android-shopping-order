package woowacourse.shopping.view.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentViewedRepository
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.toDomain
import woowacourse.shopping.model.toUiModel

class ProductDetailPresenter(
    initialQuantity: Int,
    private val productId: Int,
    private val view: ProductDetailContract.View,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentViewedRepository: RecentViewedRepository,
) : ProductDetailContract.Presenter {
    private var product: ProductModel? = null
    private var lastViewedProduct: ProductModel? = null
    private val _quantity: MutableLiveData<Int> = MutableLiveData<Int>(initialQuantity)
    override val quantity: LiveData<Int>
        get() = _quantity

    init {
        recentViewedRepository.findAll {
            lastViewedProduct = it.firstOrNull()?.toUiModel(null)
        }
    }

    override fun fetchProductDetail() {
        productRepository.getProductById(productId) {
            updateRecentViewedProducts(it.toUiModel())
            view.showProductDetail(it.toUiModel(), lastViewedProduct)
            _quantity.value = it.toUiModel().quantity
            product = it.toUiModel()
        }
    }

    override fun putInCart(product: ProductModel) {
        val quantityValue = _quantity.value
        if (quantityValue != null && quantityValue > 0) {
            if (product.cartId == null || product.cartId <= 0) {
                cartRepository.insert(product.id, quantityValue) {
                    view.finishActivity(it > 0)
                }
                return
            }
            cartRepository.update(product.cartId, quantityValue) {
                view.finishActivity(it)
            }
        }
    }

    override fun updateRecentViewedProducts(product: ProductModel) {
        recentViewedRepository.add(product.toDomain())
    }

    override fun plusCount() {
        _quantity.value = _quantity.value?.plus(1)
    }

    override fun minusCount() {
        if ((_quantity.value ?: 1) > COUNT_MIN + 1) _quantity.value = _quantity.value?.minus(1)
    }

    companion object {
        private const val COUNT_MIN = 1
    }
}
