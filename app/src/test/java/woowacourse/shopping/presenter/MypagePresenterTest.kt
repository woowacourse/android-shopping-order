package woowacourse.shopping.presenter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.data.repository.MypageRepository
import woowacourse.shopping.view.mypage.MypageContract
import woowacourse.shopping.view.mypage.MypagePresenter

class MypagePresenterTest {
    private lateinit var view: MypageContract.View
    private lateinit var presenter: MypageContract.Presenter
    private lateinit var mypageRepository: MypageRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        mypageRepository = object : MypageRepository {
            override fun getCash(callback: (Int) -> Unit) {
                callback(10000)
            }

            override fun chargeCash(cash: Int, callback: (Int) -> Unit) {
                callback(10000 + cash)
            }
        }
        presenter = MypagePresenter(view, mypageRepository)
    }

    @Test
    fun 보유한_캐시를_띄울_수_있다() {
        // given

        // when
        presenter.fetchCash()

        // then
        assertEquals(10000, presenter.cash.value)
    }

    @Test
    fun 받은_캐시가_양수면_캐시를_충전할_수_있다() {
        // given
        presenter.fetchCash()

        // when
        presenter.chargeCash(10000)

        // then
        assertEquals(20000, presenter.cash.value)
    }

    @Test
    fun 받은_캐시가_음수면_캐시를_충전할_수_없다() {
        // given
        presenter.fetchCash()

        // when
        presenter.chargeCash(-10000)

        // then
        assertEquals(10000, presenter.cash.value)
        verify(exactly = 1) { view.showNegativeIntErrorToast() }
    }
}
