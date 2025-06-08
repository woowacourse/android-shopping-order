package woowacourse.shopping.feature.login

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.domain.model.Authorization
import woowacourse.shopping.util.InstantTaskExecutorExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class LoginViewModelTest {
    @MockK
    private lateinit var cartRepository: CartRepository

    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() =
        runTest {
            Dispatchers.setMain(testDispatcher)
            MockKAnnotations.init(this@LoginViewModelTest)
            setupRepositoryMocks()
            viewModel = LoginViewModel(cartRepository)
        }

    @AfterEach
    fun tearDown() {
        runTest { }
        Dispatchers.resetMain()
    }

    private fun setupRepositoryMocks() {
        coEvery { cartRepository.checkValidLocalSavedBasicKey() } returns CartFetchResult.Success(200)
        coEvery { cartRepository.checkValidBasicKey(any()) } returns CartFetchResult.Success(200)
        coEvery { cartRepository.saveBasicKey() } returns Result.success(Unit)
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
    fun `login 호출 시 id와 pw 문자열로 생성한 BasicKey로 checkValidBasicKey가 호출된다`() =
        runTest {
            val testId = "testuser"
            val testPw = "testpass"
            Authorization.setBasicKeyByIdPw(testId, testPw)
            val expectedBasicKey = Authorization.basicKey

            coEvery { cartRepository.saveBasicKey() } returns Result.success(Unit)
            coEvery { cartRepository.checkValidBasicKey(any()) } returns CartFetchResult.Success(200)

            viewModel.id.set(testId)
            viewModel.pw.set(testPw)

            viewModel.login()

            coVerify { cartRepository.checkValidBasicKey(expectedBasicKey) }
        }

    @Test
    fun `로그인 성공 시 saveBasicKey가 호출된다`() =
        runTest {
            coEvery { cartRepository.checkValidBasicKey(any()) } returns CartFetchResult.Success(200)
            coEvery { cartRepository.saveBasicKey() } returns Result.success(Unit)

            viewModel.login()

            coVerify { cartRepository.saveBasicKey() }
        }

    @Test
    fun `잘못된 계정으로 로그인 실패시 loginErrorEvent가 NotFound로 발생한다`() =
        runTest {
            coEvery { cartRepository.checkValidBasicKey(any()) } returns CartFetchResult.Error(CartFetchError.Network)

            viewModel.id.set("wronguser")
            viewModel.pw.set("wrongpass")

            viewModel.login()

            assertThat(viewModel.loginErrorEvent.getValue()).isEqualTo(LoginError.NotFound)
        }
}
