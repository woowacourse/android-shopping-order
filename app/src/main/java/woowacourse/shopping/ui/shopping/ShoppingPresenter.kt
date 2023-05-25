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
        Thread {
            val products = productRepository.getNext(PRODUCT_COUNT).map { it.toUIModel() }
            handler.post {
                view.addMoreProducts(products)
            }
        }.start()
    }

    override fun setUpRecentProducts() {
        Thread {
            val recentProducts = recentRepository.getRecent(RECENT_PRODUCT_COUNT)
                .map { it.toUIModel() }
            handler.post {
                view.setRecentProducts(recentProducts)
            }
        }.start()
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
        view.navigateToProductDetail(
            productRepository.findById(productId).toUIModel()
        )
    }

    companion object {
        private const val RECENT_PRODUCT_COUNT = 10
        private const val PRODUCT_COUNT = 20
    }
}
