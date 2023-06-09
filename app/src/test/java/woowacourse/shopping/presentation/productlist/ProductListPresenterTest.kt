package woowacourse.shopping.presentation.productlist

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.presentation.CartFixture
import woowacourse.shopping.presentation.ProductFixture
import woowacourse.shopping.presentation.RecentProductFixture
import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.RecentProductModel
import woowacourse.shopping.presentation.model.RecentProductsModel
import woowacourse.shopping.presentation.view.productlist.ProductContract
import woowacourse.shopping.presentation.view.productlist.ProductListPresenter
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.data.repository.product.ProductRepository
import woowacouse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacouse.shopping.model.cart.CartProduct
import woowacouse.shopping.model.product.Product
import woowacouse.shopping.model.recentproduct.RecentProduct
import woowacouse.shopping.model.recentproduct.RecentProducts

class ProductListPresenterTest {
    private lateinit var presenter: ProductContract.Presenter
    private lateinit var view: ProductContract.View
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var recentProductRepository: RecentProductRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        productRepository = mockk()
        cartRepository = mockk()
        recentProductRepository = mockk()

        presenter =
            ProductListPresenter(view, productRepository, cartRepository, recentProductRepository)
    }

    @Test
    fun `데이터를 받아와 상품 목록 어댑터를 설정한다`() {
        // given
        // 장바구니 데이터를 가져온다
        every {
            cartRepository.loadAllCarts(
                onFailure = any(),
                onSuccess = captureLambda()
            )
        } answers {
            lambda<(List<CartProduct>) -> Unit>().captured.invoke(CartFixture.getFixture().map { it.toModel() })
        }

        // 상품 데이터를 가져온다
        every {
            productRepository.loadDatas(
                onFailure = any(),
                onSuccess = captureLambda()
            )
        } answers {
            lambda<(List<Product>) -> Unit>().captured.invoke(ProductFixture.getDatas().map { it.toModel() })
        }

        // 최근 본 상품 데이터를 가져온다
        every {
            recentProductRepository.getRecentProducts(
                10,
                onFailure = any(),
                onSuccess = captureLambda()
            )
        } answers {
            lambda<(RecentProducts) -> Unit>().captured.invoke(RecentProductFixture.getDatas().toModel())
        }

        // when
        presenter.initProductItems()
    }

    @Test
    fun `최근 본 상품을 저장한다`() {
        // given
        justRun { recentProductRepository.addCart(1L) }

        // when
        presenter.saveRecentProduct(1L)

        // then
        verify { recentProductRepository.addCart(1L) }
    }

    @Test
    fun `업데이트 된 데이터를 받아와 최근 본 상품을 갱신한다`() {
        // given
        // 최근 본 상품 데이터를 가져온다
        every {
            recentProductRepository.getRecentProducts(
                10,
                onFailure = any(),
                onSuccess = captureLambda()
            )
        } answers {
            lambda<(RecentProducts) -> Unit>().captured.invoke(RecentProductFixture.getDatas().toModel())
        }

        justRun { view.showRecentProductItemsView(RecentProductFixture.getDatas().recentProducts) }

        // when
        presenter.loadRecentProductItems()

        // then
        verify { view.showRecentProductItemsView(RecentProductFixture.getDatas().recentProducts) }
    }

    @Test
    fun `데이터가 더 존재한다면 추가 데이터를 가져와 갱신한다`() {
        // given
        val slotDisplayProducts = slot<List<CartProductModel>>()
        justRun {
            view.showProductItemsView(capture(slotDisplayProducts))
        }

        // when
        presenter.updateProductItems(startIndex = 0)

        // then
        verify { view.showProductItemsView(slotDisplayProducts.captured) }
    }

    @Test
    fun `Cart 옵션을 누르면 장바구니 화면을 보여준다`() {
        // given
        justRun { view.moveToCartView() }

        // when
        presenter.actionOptionItem()

        // then
        verify { view.moveToCartView() }
    }

    @Test
    fun `최근 본 상품의 스크롤 위치를 저장한다`() {
        presenter.updateRecentProductsLastScroll(100)
        val actual = presenter.getRecentProductsLastScroll()
        val expected = 100
        assertEquals(expected, actual)
    }

    @Test
    fun `마지막으로 본 상품 정보를 가져온다`() {
        // given
        every {
            recentProductRepository.getRecentProducts(
                10,
                onFailure = any(),
                captureLambda()
            )
        } answers {
            lambda<(RecentProducts) -> Unit>().captured.invoke(RecentProductFixture.getDatas().toModel())
        }

        presenter.loadRecentProductItems()

        // when
        val actual = presenter.getLastRecentProductItem(0)

        // then
        val expected =
            RecentProductModel(
                1L,
                ProductModel(
                    id = 1L,
                    title = "[선물세트][밀크바오밥] 퍼퓸 화이트 4종 선물세트 (샴푸+트리트먼트+바디워시+바디로션)",
                    price = 24_900,
                    imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
                )
            )
        assertEquals(expected, actual)
    }

    @Test
    fun `상품 아이디가 3인 장바구니 상품 개수가 0이면 해당 장바구니 상품 개수를 갱신한다`() {
        // given
        // 상품 정보를 초기화 한다
        presenter.setCartProductItems(CartFixture.getFixture())

        // 상품 개수가 0이면 상품 개수를 갱신한다
        every {
            cartRepository.addCartProduct(
                3L,
                onFailure = any(),
                onSuccess = captureLambda()
            )
        } answers {
            lambda<(Long) -> Unit>().captured.invoke(3L)
        }

        justRun { view.updateToolbarCartCountView(3) }
        justRun { view.setVisibleToolbarCartCountView() }

        // when
        presenter.updateCount(3L, 1)

        // then
        verify { view.updateToolbarCartCountView(3) }
        verify { view.setVisibleToolbarCartCountView() }
    }

    private fun RecentProductsModel.toModel(): RecentProducts {
        return RecentProducts(
            recentProducts.map {
                RecentProduct(it.id, it.product.toModel())
            }
        )
    }
}
