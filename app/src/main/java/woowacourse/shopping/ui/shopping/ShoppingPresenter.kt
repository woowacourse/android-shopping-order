package woowacourse.shopping.ui.shopping

import android.os.Handler
import android.os.Looper
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentRepository

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val cartRepository: CartRepository
) : ShoppingContract.Presenter {
    private val handler = Handler(Looper.getMainLooper())

    override fun setUpProducts() {
        setUpCartCounts()
        setUpNextProducts()
        setUpRecentProducts()
    }

    override fun setUpCartCounts() {
        Thread {
            val cartProducts = cartRepository.getAll().all()
                .associateBy { it.productId }
                .mapValues { it.value.count }
            handler.post {
                view.setCartProducts(cartProducts)
            }
        }.start()
    }

    override fun setUpNextProducts() {
        productRepository.getAll { products ->
            products?.let {
                view.addMoreProducts(products.map { it.toUIModel() })
            }
        }
    }

    override fun setUpRecentProducts() {
        val recentProducts = recentRepository.getRecent(RECENT_PRODUCT_COUNT)
            .map { it.toUIModel() }
        view.setRecentProducts(recentProducts)
    }

    override fun setUpTotalCount() {
        Thread {
            val totalSelectedCount = cartRepository.getTotalSelectedCount()
            handler.post {
                view.setToolbar(totalSelectedCount)
            }
        }.start()
    }

    override fun updateItemCount(productId: Int, count: Int) {
        cartRepository.insert(productId)
        cartRepository.updateCount(productId, count)
    }

    override fun navigateToItemDetail(productId: Int) {
        productRepository.findById(productId) {
            it?.let {
                recentRepository.insert(it)
            }
        }
    }

    companion object {
        private const val RECENT_PRODUCT_COUNT = 10
        private const val PRODUCT_COUNT = 20
    }
}
