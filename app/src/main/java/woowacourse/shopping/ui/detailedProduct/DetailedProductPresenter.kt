package woowacourse.shopping.ui.detailedProduct

import java.util.concurrent.CompletableFuture
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

    init {
        CompletableFuture.supplyAsync {
            productRepository.findById(productId)
        }.thenAccept { result ->
            result.onSuccess { product -> this.product = product.toUIModel() }
                .onFailure { exception -> LogUtil.logError(exception) }
        }
    }

    override fun setUpLastProduct() {
        val lastId = sharedPreferenceUtils.getLastProductId()
        if (lastId == product.id || lastId == -1) {
            lastProduct = null
            return
        }

        CompletableFuture.supplyAsync {
            productRepository.findById(lastId)
        }.thenAccept { result ->
            result.onSuccess { product -> this.lastProduct = product.toUIModel() }
                .onFailure { exception -> LogUtil.logError(exception) }
        }
        sharedPreferenceUtils.setLastProductId(product.id)
    }

    override fun setUpProductDetail() {
        view.setProductDetail(product, lastProduct)
    }

    override fun addProductToCart(count: Int) {
        CompletableFuture.supplyAsync {
            cartRepository.updateCountWithProductId(product.id, count)
        }.thenAccept { result ->
            result.onSuccess { view.navigateToCart() }
                .onFailure { exception -> LogUtil.logError(exception) }
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
        view.navigateToAddToCartDialog(product)
    }
}
