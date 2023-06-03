package woowacourse.shopping.ui.detailedProduct

import java.util.concurrent.locks.ReentrantLock
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.utils.LogUtil
import woowacourse.shopping.utils.SharedPreferenceUtils

class DetailedProductPresenter(
    private val view: DetailedProductContract.View,
    private val sharedPreferenceUtils: SharedPreferenceUtils,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentRepository: RecentRepository,
    productId: Int
) : DetailedProductContract.Presenter {
    private lateinit var product: ProductUIModel
    private var lastProduct: ProductUIModel? = null

    private val lock = ReentrantLock()

    init {
        lock.lock()
        productRepository.findById(productId) { result ->
            result.onSuccess { product -> this.product = product.toUIModel() }
                .onFailure { exception -> LogUtil.logError(exception) }
                .also { lock.unlock() }
        }
        while (lock.isLocked) { Thread.sleep(100) }

        if (!this::product.isInitialized) {
            view.showProductNotFound()
        }
    }

    override fun setUpLastProduct() {
        sharedPreferenceUtils.getLastProductId()
            .takeIf { it != product.id && it != -1 }
            ?.let {
                productRepository.findById(it) { result ->
                    result.onSuccess { product -> lastProduct = product.toUIModel() }
                        .onFailure { exception -> LogUtil.logError(exception) }
                }
            }
        sharedPreferenceUtils.setLastProductId(product.id)
    }

    override fun setUpProductDetail() {
        view.setProductDetail(product, lastProduct)
    }

    override fun addProductToCart(count: Int) {
        cartRepository.insert(product.id)
        cartRepository.updateCountWithProductId(product.id, count) {
            view.navigateToCart()
        }
    }

    override fun addProductToRecent() {
        recentRepository.findById(product.id)?.let {
            recentRepository.delete(it.id)
        }
        recentRepository.insert(product.toDomain())
    }

    override fun navigateToDetailedProduct() {
        lastProduct?.let {
            sharedPreferenceUtils.setLastProductId(-1)
            view.navigateToDetailedProduct(it.id)
        }
    }

    override fun navigateToAddToCartDialog() {
        cartRepository.insert(product.toDomain().id)
        view.navigateToAddToCartDialog(product)
    }
}
