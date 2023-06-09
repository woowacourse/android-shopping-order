/*
package woowacourse.shopping

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.repository.ChargeRepository
import woowacourse.shopping.domain.util.WoowaResult
import woowacourse.shopping.presentation.ui.myPage.MyPageContract
import woowacourse.shopping.presentation.ui.myPage.MyPagePresenter

class MyPagePresenterTest {
    private lateinit var presenter: MyPageContract.Presenter
    private lateinit var view: MyPageContract.View
    private lateinit var chargeRepository: ChargeRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        chargeRepository = mockk(relaxed = true)
        presenter = MyPagePresenter(view, chargeRepository)
    }

    @Test
    fun `잔액을 가져오는데 성공하면 잔액을 보여준다`() {
        // given
        val amount: Int = 10_000
        val slot = slot<(WoowaResult<Int>) -> Unit>()
        val callback: (WoowaResult<Int>) -> Unit = {
            when (it) {
                is WoowaResult.SUCCESS -> {}
                is WoowaResult.FAIL -> {}
            }
        }
        every { chargeRepository.fetchCharge { capture(slot) } } just runs
        every { view.showCharge(amount) } just runs

        // when
        presenter.fetchCharge()

        // then
        verify(exactly = 1) { view.showCharge(amount) }
    }
}
*/
