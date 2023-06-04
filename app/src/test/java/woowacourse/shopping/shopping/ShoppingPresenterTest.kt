package woowacourse.shopping.shopping

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Test
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.MemberRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.shopping.ShoppingContract
import woowacourse.shopping.ui.shopping.ShoppingPresenter

class ShoppingPresenterTest {
    private lateinit var presenter: ShoppingPresenter
    private val view: ShoppingContract.View = mockk()
    private val productRepository: ProductRepository = mockk()
    private val recentProductRepository: RecentProductRepository = mockk()
    private val cartRepository: CartRepository = mockk()
    private val memberRepository: MemberRepository = mockk()

    @Test
    fun `포인트를 보여준다`() {
        // given
        every { productRepository.getProducts(any(), any()) } just runs
        presenter = ShoppingPresenter(
            view, productRepository, recentProductRepository, cartRepository, memberRepository, 0, 0
        )

        val points = 3000
        val successSlot = slot<(Int) -> Unit>()
        every { memberRepository.getPoints(capture(successSlot), any()) } answers {
            successSlot.captured(points)
        }
        every { view.showPoints(any()) } just runs

        // when
        presenter.loadPoints()

        // then
        verify { view.showPoints(points) }
    }

    @Test
    fun `주문 내역을 열어서 보여준다`() {
        // given
        every { productRepository.getProducts(any(), any()) } just runs
        presenter = ShoppingPresenter(
            view, productRepository, recentProductRepository, cartRepository, memberRepository, 0, 0
        )

        every { view.showOrderHistory() } just runs

        // when
        presenter.openOrderHistory()

        // then
        verify { view.showOrderHistory() }
    }
}
