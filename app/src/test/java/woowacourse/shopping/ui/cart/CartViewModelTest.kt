package woowacourse.shopping.ui.cart

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.testing.MainDispatcherExtension
import woowacourse.shopping.testing.fakes.FakeCartRepository
import woowacourse.shopping.ui.event.UiEvent

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()
    private lateinit var viewModel: CartViewModel
    private lateinit var fakeCartRepository: FakeCartRepository

    @BeforeEach
    fun initViewModel() {
        fakeCartRepository = FakeCartRepository()

        viewModel = CartViewModel(
            cartRepository = fakeCartRepository
        )
    }

    @Test
    fun `현재 페이지 위치에 따라 페이지 이동 가능 여부가 결정되고 페이지를 이동할 수 있다`() =
        runTest {
            insertProducts(1..6)

            viewModel = CartViewModel(fakeCartRepository)
            testScheduler.advanceUntilIdle()

            viewModel.uiState.value.run {
                assertEquals(0, currentPage)
                assertFalse(previousEnable)
                assertTrue(nextEnable)
            }

            viewModel.next()
            testScheduler.advanceUntilIdle()

            viewModel.uiState.value.run {
                assertEquals(1, currentPage)
                assertTrue(previousEnable)
                assertFalse(nextEnable)
            }

            viewModel.prev()
            testScheduler.advanceUntilIdle()

            assertEquals(0, viewModel.uiState.value.currentPage)
        }

    @Test
    fun `현재 페이지에 해당하는 상품만큼 서버에서 불러와 uiState를 구성한다`() =
        runTest {
            insertProducts(1..7)

            viewModel = CartViewModel(fakeCartRepository)
            testScheduler.advanceUntilIdle()

            assertEquals(5, viewModel.uiState.value.cartItems.size)

            viewModel.next()
            testScheduler.advanceUntilIdle()

            assertEquals(2, viewModel.uiState.value.cartItems.size)
        }

    @Test
    fun `마지막 페이지의 아이템을 모두 삭제하면 자동으로 이전 페이지로 이동한다`() =
        runTest {
            insertProducts(1..6)

            viewModel = CartViewModel(fakeCartRepository)
            testScheduler.advanceUntilIdle()

            viewModel.next()
            testScheduler.advanceUntilIdle()
            assertEquals(1, viewModel.uiState.value.currentPage)

            val event = async(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.first() }
            viewModel.removeWithID(6L)
            assertEquals(UiEvent.ShowMessage("상품을 삭제했습니다."), event.await())
            testScheduler.advanceUntilIdle()

            assertEquals(0, viewModel.uiState.value.currentPage)
        }

    @Test
    fun `수량 변경 시 최종 수량이 서버에 전달된다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(1L, product(id = 1L), 5))

            viewModel = CartViewModel(fakeCartRepository)
            testScheduler.advanceUntilIdle()

            viewModel.updateCountWithID(1L, 1)
            testScheduler.advanceUntilIdle()

            assertEquals(6, viewModel.firstCartItemQuantity())

            viewModel.updateCountWithID(1L, -2)
            testScheduler.advanceUntilIdle()

            assertEquals(4, viewModel.firstCartItemQuantity())
        }

    @Test
    fun `수량 변경 성공 시 장바구니 전체를 다시 조회하지 않고 로컬 상태만 갱신한다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(1L, product(id = 1L), 5))

            viewModel = CartViewModel(fakeCartRepository)
            testScheduler.advanceUntilIdle()
            fakeCartRepository.pageRequests.clear()

            viewModel.updateCountWithID(1L, 1)
            testScheduler.advanceUntilIdle()

            assertEquals(6, viewModel.firstCartItemQuantity())
            assertEquals(emptyList<FakeCartRepository.PageRequest>(), fakeCartRepository.pageRequests)
        }

    @Test
    fun `전체 장바구니 조회는 마지막 페이지까지 작은 페이지 단위로 반복한다`() =
        runTest {
            insertProducts(1..7)

            fakeCartRepository.pageRequests.clear()

            val cartItems = fakeCartRepository.getAllCartItems(pageSize = 5)

            assertEquals(7, cartItems.purchaseProducts.size)
            assertEquals(
                listOf(
                    FakeCartRepository.PageRequest(page = 0, size = 5),
                    FakeCartRepository.PageRequest(page = 1, size = 5),
                ),
                fakeCartRepository.pageRequests,
            )
        }

    @Test
    fun `수량 변경 실패 시 에러 이벤트가 발생하고 로딩이 종료된다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(1L, product(id = 1L), 5))

            viewModel = CartViewModel(fakeCartRepository)
            testScheduler.advanceUntilIdle()

            fakeCartRepository.shouldFail = true
            val event = async(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.first() }

            viewModel.updateCountWithID(1L, 1)

            assertEquals(
                UiEvent.ShowMessage("수량 변경에 실패했습니다. 다시 시도해주세요."),
                event.await(),
            )

            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `아이템 삭제 실패 시 에러 이벤트가 발생하고 로딩이 종료된다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(1L, product(id = 1L), 5))

            viewModel = CartViewModel(fakeCartRepository)
            testScheduler.advanceUntilIdle()

            fakeCartRepository.shouldFail = true
            val event = async(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.first() }

            viewModel.removeWithID(1L)

            assertEquals(
                UiEvent.ShowMessage("아이템 삭제에 실패했습니다."),
                event.await(),
            )

            assertFalse(viewModel.uiState.value.isLoading)
        }

    private fun CartViewModel.firstCartItemQuantity(): Int =
        uiState
            .value
            .cartItems
            .first()
            .quantity

    private suspend fun insertProducts(range: IntRange) {
        range.forEach {
            fakeCartRepository.insert(PurchaseProduct(it.toLong(), product(id = it.toLong()), 1))
        }
    }

    private fun product(id: Long): Product =
        Product(
            id = id,
            name = "상품$id",
            price = 1000,
            imageUri = "uri",
            category = "카테고리",
        )
}
