package woowacourse.shopping.ui.shopping

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.BasketProductFixture
import woowacourse.shopping.ui.ProductFixture
import woowacourse.shopping.ui.RecentProductFixture
import woowacourse.shopping.ui.mapper.toPriceUiModel
import woowacourse.shopping.ui.mapper.toProductUiModel
import woowacourse.shopping.ui.mapper.toRecentProductUiModel
import woowacourse.shopping.ui.model.ProductUiModel

class ShoppingPresenterTest {
    private lateinit var view: ShoppingContract.View
    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var basketRepository: BasketRepository
    private lateinit var presenter: ShoppingPresenter

    @Before
    fun initPresenter() {
        view = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)
        recentProductRepository = mockk(relaxed = true)
        basketRepository = mockk(relaxed = true)
        presenter = ShoppingPresenter(
            view = view,
            productRepository = productRepository,
            recentProductRepository = recentProductRepository,
            basketRepository = basketRepository
        )
    }

    @Test
    fun `프레젠터가 생성되면서 상품이 로드 되지 않았을 때 보이는 스켈레톤 뷰의 상태가 업데이트 된다`() {
        // given

        // when
        ShoppingPresenter(
            view = view,
            productRepository = productRepository,
            recentProductRepository = recentProductRepository,
            basketRepository = basketRepository
        )

        // then
        verify { view.updateSkeletonState(false) }
    }

    @Test
    fun `저장소로부터 장바구니 상품들을 받아와 장바구니 개수를 갱신한다`() {
        // given
        val basketProducts = BasketProductFixture.createBasketProducts()
        val slotUpdateProducts = slot<(List<BasketProduct>) -> Unit>()
        val totalBasketProductCount = basketProducts.sumOf { it.count.value }

        every {
            basketRepository.getAll(
                onReceived = capture(slotUpdateProducts),
                onFailed = any()
            )
        }.answers {
            slotUpdateProducts.captured.invoke(basketProducts)
        }

        // when
        presenter.initBasket()

        // then
        verify { view.updateTotalBasketCount(totalBasketProductCount) }
    }

    @Test
    fun `저장소로부터 상품을 받아와 상품 뷰를 갱신한다`() {
        // given
        val products = ProductFixture.createProducts()
        val slotUpdateProducts = slot<(products: List<Product>) -> Unit>()

        setUpBasket(
            products = products,
            basketProducts = listOf()
        )

        every {
            productRepository.getPartially(
                any(),
                any(),
                onReceived = capture(slotUpdateProducts)
            )
        }.answers {
            slotUpdateProducts.captured.invoke(products)
        }

        // when
        presenter.updateProducts()

        // then
        verify { view.updateProducts(products.map { it.toProductUiModel() }) }
    }

    @Test
    fun `최근 본 상품 목록을 업데이트 하면 화면 업데이트 로직도 호출된다`() {
        // given
        val products = RecentProductFixture.createProducts()

        every { recentProductRepository.getPartially(any()) } returns products

        // when
        presenter.fetchRecentProducts()

        // then
        verify { view.updateRecentProducts(products.map { it.toRecentProductUiModel() }) }
    }

    @Test
    fun `장바구니에 물품을 추가하면 데이터베이스에 저장하고 관련 데이터(상품이 장바구니에 담긴 갯수 전체 장바구니 count 수)를 업데이트 한다`() {
        // given
        val products = ProductFixture.createProducts()
        val basketProducts = BasketProductFixture.createBasketProducts()

        setUpBasket(
            products = products,
            basketProducts = basketProducts
        )

        // when
        presenter.plusBasketProductCount(products.first())

        // then
        val expected = products.map { product ->
            ProductUiModel(
                id = product.id,
                name = product.name,
                price = product.price.toPriceUiModel(),
                imageUrl = product.imageUrl,
                basketCount = basketProducts.first { it.product.id == product.id }.count.value
            )
        }

        verify { view.updateProducts(expected) }
        verify { view.updateTotalBasketCount(basketProducts.sumOf { it.count.value }) }
        verify {
            basketRepository.update(
                basketProduct = basketProducts.find { it.product.id == products.first().id }!!,
                any(),
                any()
            )
        }
    }

    @Test
    fun `장바구니에 물품을 빼면 데이터베이스에서도 빼는 로직을 실행하고 관련 데이터(상품이 장바구니에 담긴 갯수 전체 장바구니 count 수)를 업데이트 한다`() {
        // given
        val products = ProductFixture.createProducts()
        val basketProducts = BasketProductFixture.createBasketProducts()

        setUpBasket(
            products = products,
            basketProducts = basketProducts
        )

        // when
        presenter.minusBasketProductCount(products.first())

        // then
        val expected = products.map { product ->
            ProductUiModel(
                id = product.id,
                name = product.name,
                price = product.price.toPriceUiModel(),
                imageUrl = product.imageUrl,
                basketCount = basketProducts.first { it.product.id == product.id }.count.value
            )
        }

        verify { view.updateProducts(expected) }
        verify { view.updateTotalBasketCount(basketProducts.sumOf { it.count.value }) }
        verify {
            basketRepository.update(
                basketProducts.find { it.product.id == products.first().id }!!,
                any(),
                any()
            )
        }
    }

    @Test
    fun `페이지네이션의 다음페이지가 존재여부를 더보기 버튼의 visibility를 업데이트 하기위해 전달한다`() {
        // given

        // when
        presenter.fetchHasNext()

        // then
        verify { view.updateMoreButtonState(any()) }
    }

    private fun setUpBasket(
        products: List<Product>? = null,
        basketProducts: List<BasketProduct>? = null,
    ) {
        val slotUpdateProduct = slot<(products: List<Product>) -> Unit>()
        val slotUpdateBasketProducts = slot<(products: List<BasketProduct>) -> Unit>()

        every {
            productRepository.getPartially(
                any(),
                any(),
                onReceived = capture(slotUpdateProduct)
            )
        }.answers {
            products?.let {
                slotUpdateProduct.captured.invoke(it)
            }
        }

        every {
            basketRepository.getAll(
                onReceived = capture(slotUpdateBasketProducts),
                any()
            )
        }.answers {
            basketProducts?.let {
                slotUpdateBasketProducts.captured.invoke(it)
            }
        }
        presenter.initBasket()
    }
}
