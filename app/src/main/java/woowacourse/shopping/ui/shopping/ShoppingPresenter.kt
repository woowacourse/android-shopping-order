package woowacourse.shopping.ui.shopping

import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentRepository
import woowacourse.shopping.utils.ActivityUtils.showErrorMessage

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val cartRepository: CartRepository,
) : ShoppingContract.Presenter {

    override fun setUpProducts() {
        setUpCartCounts()
        setUpNextProducts()
        setUpRecentProducts()
    }

    override fun setUpCartCounts() {
        cartRepository.getAll({ carts ->
            val cartProducts = carts.all()
                .associateBy { it.product.id }
                .mapValues { it.value.quantity }
            view.setCartProducts(cartProducts)
        }, { showErrorMessage(it.message) })
    }

    override fun setUpNextProducts() {
        productRepository.getAll(
            { products ->
                products.let {
                    view.addMoreProducts(products.map { it.toUIModel() })
                }
            },
            { showErrorMessage(it.message) },
        )
    }

    override fun setUpRecentProducts() {
        val recentProducts = recentRepository.getRecent(RECENT_PRODUCT_COUNT)
            .map { it.toUIModel() }
        view.setRecentProducts(recentProducts)
    }

    override fun setUpTotalCount() {
        view.setToolbar(
            cartRepository.getTotalSelectedCount(),
        )
    }

    override fun updateItemCount(productId: Int, count: Int) {
        cartRepository.updateCount(
            productId,
            count,
            {
                cartRepository.getAll(
                    { setUpTotalCount() },
                    { showErrorMessage(it.message) },
                )
            },
            { showErrorMessage(it.message) },
        )
    }

    override fun navigateToItemDetail(productId: Int) {
        productRepository.findById(
            productId,
            { view.navigateToProductDetail(it.toUIModel()) },
            { showErrorMessage(it.message) },
        )
    }

    companion object {
        private const val RECENT_PRODUCT_COUNT = 10
    }
}
