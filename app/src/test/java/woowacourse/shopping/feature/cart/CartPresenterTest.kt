package woowacourse.shopping.feature.cart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.feature.CartFixture
import woowacourse.shopping.feature.Product
import woowacourse.shopping.feature.getOrAwaitValue
import woowacourse.shopping.mapper.toPresentation

internal class CartPresenterTest {
    private lateinit var presenter: CartContract.Presenter
    private lateinit var view: CartContract.View
    private lateinit var cartRepository: CartRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        presenter = CartPresenter(view, cartRepository)
        every {
            cartRepository.getAll(any(), any())
        } answers {
            val successBlock = arg<(List<CartProduct>) -> Unit>(0)
            successBlock(mockCartProducts)
        }
    }

    @Test
    fun `처음에 첫 페이지를 불러온다`() {
        // when
        presenter.loadInitCartProduct()

        // then
        val actual = presenter.currentPageCartProducts.getOrAwaitValue()
        val expected = mockCartProducts.take(5).map { it.toPresentation() }
        assert(expected == actual)
    }

    @Test
    fun `이전 페이지를 불러온다`() {
        // given
        presenter.loadInitCartProduct()
        presenter.loadNextPage() //  2 페이지로 이동

        // when
        presenter.loadPreviousPage() // 1페이지로 이동

        // then
        val actual = presenter.currentPageCartProducts.getOrAwaitValue()
        val expected = mockCartProducts.take(5).map { it.toPresentation() } // 1페이지 데이터들
        assert(actual == expected)
    }

    @Test
    fun `다음 페이지를 불러온다`() {
        // given
        presenter.loadInitCartProduct() // 현재 1페이지

        // when
        presenter.loadNextPage() // 2페이지로 이동

        // then
        val actual = presenter.currentPageCartProducts.getOrAwaitValue()
        val expected = mockCartProducts.subList(5, 10).map { it.toPresentation() } // 2페이지 데이터들
        assert(actual == expected)
    }

    @Test
    fun `주문아이디가 일치하는 주문 상품을 삭제한다`() {
        // given
        presenter.loadInitCartProduct()
        val slot = slot<Long>()
        every {
            cartRepository.deleteCartProduct(capture(slot), onSuccess = any(), any())
        } answers {
            val successBlock = arg<(Long) -> Unit>(1)
            successBlock(slot.captured)
        }

        // when
        presenter.handleDeleteCartProductClick(4L)

        // then
        verify { cartRepository.deleteCartProduct(4L, any(), any()) }
        val expected = 4L
        val actual = slot.captured
        assert(expected == actual)

        val actualCurrentPageCartProducts = presenter.currentPageCartProducts.getOrAwaitValue()
        val expectedCurrentPageCartProducts = CartFixture.getMockCarts(
            Triple(1L, Product(1L, 3000), 3),
            Triple(2L, Product(2L, 3000), 3),
            Triple(3L, Product(3L, 3000), 3),
            Triple(5L, Product(5L, 3000), 3),
            Triple(6L, Product(6L, 3000), 3),
        ).map { it.toPresentation() }
        assert(actualCurrentPageCartProducts == expectedCurrentPageCartProducts)
    }

    @Test
    fun `주문하기 버튼을 누르면, 주문 화면으로 넘어간다`() {
        // given
        presenter.loadInitCartProduct()
        val slot = slot<List<Long>>()
        justRun { view.navigateToOrder(capture(slot)) }

        // when
        presenter.requestOrder()

        // then
        val actual = slot.captured
        val expected = mockCartProducts.map { it.cartId }
        assert(actual == expected)
    }

    private val mockCartProducts =
        CartFixture.getMockCarts(
            Triple(1L, Product(1L, 3000), 3),
            Triple(2L, Product(2L, 3000), 3),
            Triple(3L, Product(3L, 3000), 3),
            Triple(4L, Product(4L, 3000), 3),
            Triple(5L, Product(5L, 3000), 3),
            Triple(6L, Product(6L, 3000), 3),
            Triple(7L, Product(7L, 3000), 3),
            Triple(8L, Product(8L, 3000), 3),
            Triple(9L, Product(9L, 3000), 3),
            Triple(10L, Product(10L, 3000), 3),
            Triple(11L, Product(11L, 3000), 3),
            Triple(12L, Product(12L, 3000), 3),
            Triple(13L, Product(13L, 3000), 3),
            Triple(14L, Product(14L, 3000), 3),
        )
}
