package woowacourse.shopping.ui.basket

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.ui.mapper.toDomain
import woowacourse.shopping.ui.mapper.toUi
import woowacourse.shopping.ui.model.UiBasketProduct

class BasketPresenter(
    override val view: BasketContract.View,
    private val basketRepository: BasketRepository,
    private var currentPage: Int = 1,
    private var startId: Int = 0
) : BasketContract.Presenter {
    private lateinit var basket: Basket
    private val hasNext: Boolean get() = basket.products.lastIndex >= startId + BASKET_PAGING_SIZE
    private var isLoaded: Boolean = false

    init {
        view.updateSkeletonState(isLoaded)
        updateBasket()
    }

    private fun updateBasket() {
        basketRepository.getAll {
            basket = Basket(it)
            updateBasketProducts()
            isLoaded = true
            view.updateSkeletonState(isLoaded)
        }
    }

    override fun fetchTotalCheckToCurrentPage(totalIsChecked: Boolean) {
        basket.getSubBasketByStartId(startId, BASKET_PAGING_SIZE).toggleAllCheck(totalIsChecked)
        updateOrderInformation()
        updateViewBasketProduct()
    }

    override fun updateBasketProductCheckState(basketProduct: BasketProduct) {
        basket.updateCheck(basketProduct)
        updateOrderInformation()
        view.updateTotalCheckBox(getTotalIsChecked())
    }

    private fun getTotalIsChecked(): Boolean =
        basket.getSubBasketByStartId(startId, BASKET_PAGING_SIZE).products.none { !it.checked }

    private fun updateOrderInformation() {
        view.updateTotalPrice(basket.getCheckedProductsTotalPrice())
        view.updateCheckedProductsCount(basket.getCheckedProductsCount())
    }

    override fun plusBasketProductCount(product: Product) {
        basketRepository.update(
            basket.getProductByProductId(product.id)?.plusCount() ?: throw IllegalStateException(
                NOT_EXIST_PRODUCT_ERROR
            )
        )
        basket = basket.plus(BasketProduct(count = Count(1), product = product))
        updateBasketProductViewData()
    }

    override fun minusBasketProductCount(product: Product) {
        basketRepository.update(
            basket.getProductByProductId(product.id)?.minusCount() ?: throw IllegalStateException(
                NOT_EXIST_PRODUCT_ERROR
            )
        )
        basket = basket.minus(BasketProduct(count = Count(1), product = product))
        updateBasketProductViewData()
    }

    override fun updateBasketProducts() {
        updateBasketProductViewData()
        updateOrderInformation()
    }

    override fun updatePreviousPage() {
        updateCurrentPage(false)
        if (startId - BASKET_PAGING_SIZE > 0) startId -= BASKET_PAGING_SIZE else startId = 0
        updateBasketProductViewData()
    }

    override fun updateNextPage() {
        updateCurrentPage(true)
        if (startId + BASKET_PAGING_SIZE <= basket.products.lastIndex) startId += BASKET_PAGING_SIZE
        updateBasketProductViewData()
    }

    private fun updateBasketProductViewData() {
        updateViewBasketProduct()
        view.updateNavigatorEnabled(currentPage > 1, hasNext)
    }

    private fun updateViewBasketProduct() {
        view.updateBasketProducts(
            basket.getSubBasketByStartId(
                startId,
                BASKET_PAGING_SIZE
            ).products.map { it.toUi() }
        )
    }

    override fun deleteBasketProduct(
        product: UiBasketProduct
    ) {
        basketRepository.remove(product.toDomain())
        basket = basket.remove(product.toDomain())
        amendStartId()
        updateBasketProductViewData()
    }

    private fun amendStartId() {
        if (basket.products.lastIndex < startId) {
            startId -= BASKET_PAGING_SIZE
            currentPage -= 1
            view.updateCurrentPage(currentPage)
        }
        if (startId < 0) startId = 0
        if (currentPage < 1) currentPage = 1
    }

    private fun updateCurrentPage(isIncrease: Boolean) {
        if (isIncrease) currentPage += 1 else currentPage -= 1
        view.updateCurrentPage(currentPage)
    }

    companion object {
        private const val BASKET_PAGING_SIZE = 5

        private const val NOT_EXIST_PRODUCT_ERROR = "장바구니에 담겨있지 않은 상품을 조회하였습니다."
    }
}
