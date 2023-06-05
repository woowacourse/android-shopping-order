package woowacourse.shopping.ui.basket

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.ui.mapper.toDomainModel
import woowacourse.shopping.ui.mapper.toUiModel
import woowacourse.shopping.ui.model.BasketProductUiModel

class BasketPresenter(
    override val view: BasketContract.View,
    private val basketRepository: BasketRepository,
    private var currentPage: Int = 1,
    private var startId: Int = 0,
) : BasketContract.Presenter {
    override lateinit var basket: Basket
    private val hasNext: Boolean get() = basket.products.lastIndex >= startId + BASKET_PAGING_SIZE
    private var isLoaded: Boolean = false

    init {
        view.updateSkeletonState(isLoaded)
        updateBasket()
    }

    private fun updateBasket() {
        basketRepository.getAll(
            onReceived = {
                basket = Basket(it)
                updateBasketProducts()
                isLoaded = true
                view.updateSkeletonState(isLoaded)
            },
            onFailed = { errorMessage ->
                view.showErrorMessage(errorMessage)
            }
        )
    }

    override fun fetchTotalCheckToCurrentPage(totalIsChecked: Boolean) {
        basket.getSubBasketByStartId(startId, BASKET_PAGING_SIZE).toggleAllCheck(totalIsChecked)
        updateOrderInformation()
        updateViewBasketProduct()
    }

    override fun updateBasketProductCheckState(basketProduct: BasketProduct) {
        basket.updateCheck(basketProduct)
        updateOrderInformation()
        updateStateToOrder()
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
            basketProduct = basket.getProductByProductId(product.id)?.plusCount()
                ?: throw IllegalStateException(
                    NOT_EXIST_PRODUCT_ERROR
                ),
            onUpdated = {
                basket = basket.plus(BasketProduct(count = Count(1), product = product))
                updateBasketProductViewData()
            },
            onFailed = { errorMessage ->
                view.showErrorMessage(errorMessage)
            }
        )
    }

    override fun minusBasketProductCount(product: Product) {
        basketRepository.update(
            basketProduct = basket.getProductByProductId(product.id)?.minusCount()
                ?: throw IllegalStateException(
                    NOT_EXIST_PRODUCT_ERROR
                ),
            onUpdated = {
                basket = basket.minus(BasketProduct(count = Count(1), product = product))
                updateBasketProductViewData()
            },
            onFailed = { errorMessage ->
                view.showErrorMessage(errorMessage)
            }
        )
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
            ).products.map { it.toUiModel() }
        )
    }

    override fun deleteBasketProduct(
        product: BasketProductUiModel,
    ) {
        basketRepository.remove(
            basketProduct = product.toDomainModel(),
            onRemoved = {
                basket = basket.remove(product.toDomainModel())
                amendStartId()
                updateBasketProductViewData()
            },
            onFailed = { errorMessage ->
                view.showErrorMessage(errorMessage)
            }
        )
    }

    private fun updateStateToOrder() {
        val hasCheckedProduct = basket.products.any { it.checked }

        view.updateOrderButtonState(hasCheckedProduct)
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

    override fun startPayment() {
        val products = basket.products
            .filter { it.checked }
            .map { it.toUiModel() }

        view.showPaymentView(
            basketProducts = products,
            totalPrice = basket.getCheckedProductsTotalPrice()
        )
    }

    companion object {
        private const val BASKET_PAGING_SIZE = 5

        private const val NOT_EXIST_PRODUCT_ERROR = "장바구니에 담겨있지 않은 상품을 조회하였습니다."
    }
}
