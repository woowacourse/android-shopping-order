package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.mapper.toProductDomainModel
import woowacourse.shopping.ui.mapper.toProductUiModel
import woowacourse.shopping.ui.mapper.toRecentProductUiModel
import woowacourse.shopping.ui.model.ProductUiModel
import woowacourse.shopping.ui.model.RecentProductUiModel
import woowacourse.shopping.util.secondOrNull
import kotlin.concurrent.thread

class ShoppingPresenter(
    override val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val basketRepository: BasketRepository,
    private var hasNext: Boolean = false,
    private var lastId: Int = -1,
    private var totalProducts: List<ProductUiModel> = listOf(),
    private var recentProducts: List<RecentProductUiModel> = listOf(),
) : ShoppingContract.Presenter {
    private lateinit var basket: Basket
    private var isLoaded: Boolean = false

    init {
        view.updateSkeletonState(isLoaded)
        initBasket()
    }

    private fun fetchBasketCount() {
        totalProducts = totalProducts.map {
            ProductUiModel(
                it.id,
                it.name,
                it.price,
                it.imageUrl,
                basket.getCountByProductId(it.id)
            )
        }
    }

    override fun initBasket() {
        basketRepository.getAll(
            onReceived = { basketProducts ->
                basket = Basket(basketProducts)
                fetchTotalBasketCount()
                updateProducts()
            },
            onFailed = { errorMessage ->
                view.showErrorMessage(errorMessage)
            }
        )
    }

    override fun updateBasket() {
        basketRepository.getAll(
            onReceived = { basketProducts ->
                basket = Basket(basketProducts)
                fetchTotalBasketCount()
                fetchBasketCount()
                view.updateProducts(totalProducts)
            },
            onFailed = { errorMessage ->
                view.showErrorMessage(errorMessage)
            }
        )
    }

    override fun fetchTotalBasketCount() {
        view.updateTotalBasketCount(basket.products.fold(0) { acc, basketProduct -> acc + basketProduct.count.value })
    }

    override fun plusBasketProductCount(product: Product) {
        basketRepository.update(
            basket.getProductByProductId(product.id)?.plusCount() ?: throw IllegalStateException(
                NOT_EXIST_PRODUCT_ERROR
            )
        )
        basket = basket.plus(BasketProduct(count = Count(1), product = product))
        fetchBasketCount()
        fetchTotalBasketCount()
        view.updateProducts(totalProducts)
    }

    override fun minusBasketProductCount(product: Product) {
        basketRepository.update(
            basket.getProductByProductId(product.id)?.minusCount() ?: throw IllegalStateException(
                NOT_EXIST_PRODUCT_ERROR
            )
        )
        basket = basket.minus(BasketProduct(count = Count(1), product = product))
        fetchBasketCount()
        fetchTotalBasketCount()
        view.updateProducts(totalProducts)
    }

    override fun addBasketProduct(product: Product) {
        basketRepository.add(
            product = product,
            onAdded = { basketProductId ->
                basket = basket.plus(
                    BasketProduct(
                        id = basketProductId,
                        count = Count(1),
                        product = product
                    )
                )
                fetchBasketCount()
                fetchTotalBasketCount()
                view.updateProducts(totalProducts)
            },
            onFailed = { errorMessage ->
                view.showErrorMessage(errorMessage)
            }
        )
    }

    override fun updateProducts() {
        productRepository
            .getPartially(TOTAL_LOAD_PRODUCT_SIZE_AT_ONCE, lastId) { products ->
                var uiProducts = products.map { it.toProductUiModel() }
                lastId = uiProducts.maxOfOrNull { it.id } ?: -1
                hasNext = checkHasNext(uiProducts)
                lastId -= if (hasNext) 1 else 0
                if (hasNext) uiProducts = uiProducts.dropLast(1)
                totalProducts += uiProducts
                fetchBasketCount()
                view.updateProducts(totalProducts)
                isLoaded = true
                view.updateSkeletonState(isLoaded)
            }
    }

    private fun checkHasNext(products: List<ProductUiModel>): Boolean =
        products.size == TOTAL_LOAD_PRODUCT_SIZE_AT_ONCE

    override fun fetchRecentProducts() {
        recentProducts = recentProductRepository.getPartially(RECENT_PRODUCT_SIZE)
            .map { it.toRecentProductUiModel() }
        view.updateRecentProducts(recentProducts)
    }

    override fun inquiryProductDetail(product: ProductUiModel) {
        val previousProduct = if (recentProducts.firstOrNull()?.product == product) {
            recentProducts.secondOrNull()?.product
        } else {
            recentProducts.firstOrNull()?.product
        }

        view.showProductDetail(
            currentProduct = product,
            currentProductBasketId = basket.getProductByProductId(product.id)?.id,
            previousProduct = previousProduct,
            previousProductBasketId = if (previousProduct != null) basket.getProductByProductId(
                previousProduct.id
            )?.id else null
        )
        thread { recentProductRepository.add(product.toProductDomainModel()) }
    }

    override fun fetchHasNext() {
        view.updateMoreButtonState(hasNext)
    }

    companion object {
        private const val RECENT_PRODUCT_SIZE = 10
        private const val LOAD_PRODUCT_SIZE_AT_ONCE = 20
        private const val PRODUCT_SIZE_FOR_HAS_NEXT = 1
        private const val TOTAL_LOAD_PRODUCT_SIZE_AT_ONCE =
            LOAD_PRODUCT_SIZE_AT_ONCE + PRODUCT_SIZE_FOR_HAS_NEXT

        private const val NOT_EXIST_PRODUCT_ERROR = "장바구니에 담겨있지 않은 상품을 조회하였습니다."
    }
}
