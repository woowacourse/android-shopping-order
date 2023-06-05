package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentProducts
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.mapper.toDomainModel
import woowacourse.shopping.ui.mapper.toUiModel
import woowacourse.shopping.ui.model.ProductUiModel
import kotlin.concurrent.thread

class ShoppingPresenter(
    override val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val basketRepository: BasketRepository,
    private var hasNext: Boolean = false,
    private var lastId: Int = -1,
    private var totalProducts: List<ProductUiModel> = listOf(),
    private var recentProducts: RecentProducts = RecentProducts(listOf()),
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
        basketRepository.getAll().thenAccept { basketProducts ->
            basket = Basket(basketProducts.getOrThrow())
            fetchTotalBasketCount()
            updateProducts()
        }.exceptionally { error ->
            error.message?.let {
                view.showErrorMessage(it)
            }
            null
        }
    }

    override fun updateBasket() {
        basketRepository.getAll().thenAccept { basketProducts ->
            basket = Basket(basketProducts.getOrThrow())
            updateBothProducts()
        }.exceptionally { error ->
            error.message?.let {
                view.showErrorMessage(it)
            }
            null
        }
    }

    override fun fetchTotalBasketCount() {
        view.updateTotalBasketCount(basket.products.fold(0) { acc, basketProduct -> acc + basketProduct.count.value })
    }

    override fun plusBasketProductCount(product: Product) {
        basketRepository.update(
            basketProduct = basket.getProductByProductId(product.id).plusCount()
        ).thenAccept {
            it.getOrThrow()
            basket = basket.plus(BasketProduct(count = Count(1), product = product))
            updateBothProducts()
        }.exceptionally { error ->
            error.message?.let { view.showErrorMessage(it) }
            null
        }
    }

    override fun minusBasketProductCount(product: Product) {
        basketRepository.update(
            basketProduct = basket.getProductByProductId(product.id).minusCount()
        ).thenAccept {
            it.getOrThrow()
            basket = basket.minus(BasketProduct(count = Count(1), product = product))
            updateBothProducts()
        }.exceptionally { error ->
            error.message?.let { view.showErrorMessage(it) }
            null
        }
    }

    override fun addBasketProduct(product: Product) {
        basketRepository.add(product).thenAccept { basketProductId ->
            basket = basket.plus(
                BasketProduct(
                    id = basketProductId.getOrThrow(),
                    count = Count(1),
                    product = product
                )
            )
            fetchBasketCount()
            fetchTotalBasketCount()
            view.updateProducts(totalProducts)
        }.exceptionally { error ->
            error.message?.let { view.showErrorMessage(it) }
            null
        }
    }

    override fun updateProducts() {
        productRepository.getPartially(TOTAL_LOAD_PRODUCT_SIZE_AT_ONCE, lastId)
            .thenAccept { products ->
                var uiProducts = products.getOrThrow().map { it.toUiModel() }
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

    private fun updateBothProducts() {
        fetchBasketCount()
        fetchTotalBasketCount()
        view.updateProducts(totalProducts)
    }

    private fun checkHasNext(products: List<ProductUiModel>): Boolean =
        products.size == TOTAL_LOAD_PRODUCT_SIZE_AT_ONCE

    override fun fetchRecentProducts() {
        recentProducts = RecentProducts(
            values = recentProductRepository.getPartially(RECENT_PRODUCT_SIZE)
        )

        view.updateRecentProducts(
            recentProducts = recentProducts.values.map {
                it.toUiModel()
            }
        )
    }

    override fun inquiryProductDetail(product: ProductUiModel) {
        val previousProduct = recentProducts.getLatestProduct(
            product = product.toDomainModel()
        )?.toUiModel()

        val previousBasketId = previousProduct?.run {
            basket.getProductByProductId(id).id
        }

        val currentProductBasketId = runCatching {
            basket.getProductByProductId(product.id).id
        }.getOrNull()

        view.showProductDetail(
            currentProduct = product,
            currentProductBasketId = currentProductBasketId,
            previousProduct = previousProduct,
            previousProductBasketId = previousBasketId
        )
        thread { recentProductRepository.add(product.toDomainModel()) }
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
    }
}
