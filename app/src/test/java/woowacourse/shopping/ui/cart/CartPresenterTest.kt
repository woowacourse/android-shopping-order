package woowacourse.shopping.ui.cart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.uimodel.PageUIModel

class CartPresenterTest {

    private lateinit var view: CartContract.View
    private lateinit var presenter: CartContract.Presenter
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository

    private val fakeProduct = Product(
        1,
        "aa",
        1,
        "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1648206780555l0.jpeg",
    )

    private val fakeCartProduct = CartProduct(
        id = 2,
        quantity = 3,
        product = fakeProduct,
    )

    private val fakeCartProducts = CartProducts(List(10) { fakeCartProduct })

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        productRepository = mockk()
        presenter = CartPresenter(view, cartRepository, 0)
    }

    @Test
    fun `화면의 초기값들을 설정한다`() {
        // given
        val slot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getPage(any(), any(), capture(slot)) } answers {
            slot.captured(fakeCartProducts)
        }
        every { cartRepository.getTotalPrice() } returns 12000
        every { cartRepository.getTotalSelectedCount() } returns 10
        every { cartRepository.hasNextPage(any(), any()) } returns true
        every { cartRepository.hasPrevPage(any(), any()) } returns true

        // when
        presenter.setUpView()

        // then
        verify(exactly = 1) {
            view.setPage(
                fakeCartProducts.toUIModel(),
                PageUIModel(pageNext = true, pagePrev = true, pageNumber = 1),
            )
        }
        assertEquals(presenter.totalPrice.value, 12000)
        assertEquals(presenter.checkedCount.value, 10)
        assertEquals(presenter.allCheck.value, true)
    }

    @Test
    fun `현재 페이지의 상품들을 모두 선택한다`() {
        // given
        val slot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getPage(any(), any(), capture(slot)) } answers {
            slot.captured(fakeCartProducts)
        }
        every { cartRepository.getTotalPrice() } returns 12000
        every { cartRepository.getTotalSelectedCount() } returns 10
        every { cartRepository.hasNextPage(any(), any()) } returns true
        every { cartRepository.hasPrevPage(any(), any()) } returns true

        // when
        presenter.setUpView()
        presenter.setUpProductsCheck(true)

        // then
        verify(exactly = 10) {
            cartRepository.updateChecked(fakeCartProduct.id, true)
        }
        verify {
            view.setPage(
                fakeCartProducts.toUIModel(),
                PageUIModel(pageNext = true, pagePrev = true, pageNumber = 1),
            )
        }
    }

    @Test
    fun `다음 페이지로 넘어가면 새로 불러온다`() {
        // given
        val slot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getPage(any(), any(), capture(slot)) } answers {
            slot.captured(fakeCartProducts)
        }
        every { cartRepository.getTotalPrice() } returns 12000
        every { cartRepository.getTotalSelectedCount() } returns 10
        every { cartRepository.hasNextPage(any(), any()) } returns true
        every { cartRepository.hasPrevPage(any(), any()) } returns true

        // when
        presenter.setUpView()
        presenter.moveToPageNext()

        // then
        verify(exactly = 1) {
            view.setPage(
                fakeCartProducts.toUIModel(),
                PageUIModel(pageNext = true, pagePrev = true, pageNumber = 2),
            )
        }
        assertEquals(presenter.allCheck.value, true)
    }

    @Test
    fun `이전 페이지로 가면 새로 불러온다`() {
        // given
        val slot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getPage(any(), any(), capture(slot)) } answers {
            slot.captured(fakeCartProducts)
        }
        every { cartRepository.getTotalPrice() } returns 12000
        every { cartRepository.getTotalSelectedCount() } returns 10
        every { cartRepository.hasNextPage(any(), any()) } returns true
        every { cartRepository.hasPrevPage(any(), any()) } returns true

        // when
        presenter.setUpView()
        presenter.moveToPagePrev()

        // then
        verify(exactly = 1) {
            view.setPage(
                fakeCartProducts.toUIModel(),
                PageUIModel(pageNext = true, pagePrev = true, pageNumber = 0),
            )
        }
        assertEquals(presenter.allCheck.value, true)
    }

    @Test
    fun `아이템의 개수를 변경한다`() {
        // given
        val slot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getPage(any(), any(), capture(slot)) } answers {
            slot.captured(fakeCartProducts)
        }
        every { cartRepository.hasNextPage(any(), any()) } returns true
        every { cartRepository.hasPrevPage(any(), any()) } returns true
        every { cartRepository.getTotalPrice() } returns 12000
        every { cartRepository.getTotalSelectedCount() } returns 10

        // when
        presenter.setUpView()
        presenter.updateItemCount(1, 0)

        // then
        verify(exactly = 1) { cartRepository.updateCount(1, 0, any()) }
        verify(exactly = 1) { view.setPage(any(), any()) }
    }

    @Test
    fun `아이템의 체크를 변경한다`() {
        // given
        every { cartRepository.hasNextPage(any(), any()) } returns true
        every { cartRepository.hasPrevPage(any(), any()) } returns true
        every { cartRepository.getTotalPrice() } returns 12000
        every { cartRepository.getTotalSelectedCount() } returns 10

        // when
        presenter.setUpView()
        presenter.updateItemCheck(1, true)

        // then
        verify(exactly = 1) { cartRepository.updateChecked(1, true) }
    }

    @Test
    fun `장바구니에 담긴 상품을 삭제한다`() {
        // given
        every { cartRepository.remove(any(), any()) } returns Unit
        every { cartRepository.getPage(any(), any(), any()) } just Runs
        every { cartRepository.hasNextPage(any(), any()) } returns true
        every { cartRepository.hasPrevPage(any(), any()) } returns true
        every { cartRepository.getTotalPrice() } returns 12000
        every { cartRepository.getTotalSelectedCount() } returns 10

        // when
        presenter.setUpView()
        presenter.removeItem(1)

        // then
        verify(exactly = 1) { cartRepository.remove(1, any()) }
    }

    @Test
    fun `상세 페이지로 이동한다`() {
        // given
        val slot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getAll(capture(slot)) } answers {
            slot.captured(fakeCartProducts)
        }

        // when
        presenter.navigateToItemDetail(fakeProduct.toUIModel().id)

        // then
        verify(exactly = 1) { view.navigateToItemDetail(fakeProduct.toUIModel()) }
    }

    @Test
    fun `현재 페이지 인덱스를 반환한다`() {
        // given
        val index = 5

        // when
        val presenter = CartPresenter(view, cartRepository, index)

        // then
        assertEquals(presenter.getPageIndex(), index)
    }
}
