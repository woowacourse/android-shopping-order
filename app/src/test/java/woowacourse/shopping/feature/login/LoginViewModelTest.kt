package woowacourse.shopping.feature.login

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.domain.model.Authorization
import woowacourse.shopping.util.InstantTaskExecutorExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class LoginViewModelTest {
    @MockK
    private lateinit var cartRepository: CartRepository

    private lateinit var viewModel: LoginViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        setupRepositoryMocks()
        viewModel = LoginViewModel(cartRepository)
    }

    private fun setupRepositoryMocks() {
        every { cartRepository.checkValidBasicKey(any(), any(), any()) } answers {
            secondArg<(Int) -> Unit>()(200)
        }
    }

    @Test
    fun `초기 상태에서 id와 pw가 빈 문자열로 설정된다`() {
        assertThat(viewModel.id.get()).isEqualTo("")
        assertThat(viewModel.pw.get()).isEqualTo("")
    }

    @Test
    fun `id와 pw 설정이 올바르게 동작한다`() {
        val testId = "testuser"
        val testPw = "testpass"

        viewModel.id.set(testId)
        viewModel.pw.set(testPw)

        assertThat(viewModel.id.get()).isEqualTo(testId)
        assertThat(viewModel.pw.get()).isEqualTo(testPw)
    }

    @Test
    fun `login 호출 시 repository checkValidBasicKey가 호출된다`() {
        viewModel.id.set("testuser")
        viewModel.pw.set("testpass")

        viewModel.login()

        verify { cartRepository.checkValidBasicKey(any(), any(), any()) }
    }

    @Test
    fun `login 호출 시 올바른 BasicKey로 repository가 호출된다`() {
        val testId = "testuser"
        val testPw = "testpass"
        val expectedBasicKey = Authorization.getBasicKey(testId, testPw)

        viewModel.id.set(testId)
        viewModel.pw.set(testPw)

        viewModel.login()

        verify { cartRepository.checkValidBasicKey(eq(expectedBasicKey), any(), any()) }
    }

    @Test
    fun `정확한 게정으로 로그인 성공시(200) loginSuccessEvent가 발생한다`() {
        val testId = "testuser"
        val testPw = "testpass"
        val expectedBasicKey = Authorization.getBasicKey(testId, testPw)

        every { cartRepository.checkValidBasicKey(any(), any(), any()) } answers {
            secondArg<(Int) -> Unit>()(200)
        }
        viewModel.id.set(testId)
        viewModel.pw.set(testPw)

        viewModel.login()

        assertThat(viewModel.loginSuccessEvent.getValue()).isEqualTo(expectedBasicKey)
    }

    @Test
    fun `잘못된 게정으로 로그인 실패시(401) loginErrorEvent가 NotFound로 발생한다`() {
        every { cartRepository.checkValidBasicKey(any(), any(), any()) } answers {
            secondArg<(Int) -> Unit>()(401)
        }
        viewModel.id.set("wronguser")
        viewModel.pw.set("wrongpass")

        viewModel.login()

        assertThat(viewModel.loginErrorEvent.getValue()).isEqualTo(LoginError.NotFound)
    }
}
