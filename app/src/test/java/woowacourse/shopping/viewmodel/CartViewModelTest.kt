package woowacourse.shopping.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.viewmodel.CartViewModel
import woowacourse.shopping.viewmodel.fakes.FakeCartRepository

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

        viewModel =
            CartViewModel(
                cartRepository = fakeCartRepository,
            )
    }

    @Test
    fun `현재 페이지 위치에 따라 페이지 이동 가능 여부가 결정되고 페이지를 이동할 수 있다`() =
        runTest {
            val products =
                (1..6).map {
                    Product(id = it.toLong(), name = "상품$it", price = 1000, imageUri = "uri", category = "카테고리")
                }
            products.forEach { fakeCartRepository.insert(PurchaseProduct(it.id, it, 1)) }

            viewModel = CartViewModel(fakeCartRepository)
            testScheduler.advanceUntilIdle()

            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.collect { }
            }

            assertEquals(0, viewModel.uiState.value.currentPage)
            assertFalse(viewModel.uiState.value.isPrevEnable)
            assertTrue(viewModel.uiState.value.isNextEnable)

            viewModel.next()
            assertEquals(1, viewModel.uiState.value.currentPage)
            assertTrue(viewModel.uiState.value.isPrevEnable)
            assertFalse(viewModel.uiState.value.isNextEnable)

            viewModel.prev()
            assertEquals(0, viewModel.uiState.value.currentPage)
        }

    @Test
    fun `현재 페이지에 해당하는 상품만큼 서버에서 불러와 pagedCart를 구성한다`() =
        runTest {
            val products =
                (1..7).map {
                    Product(id = it.toLong(), name = "상품$it", price = 1000, imageUri = "uri", category = "카테고리")
                }
            products.forEach { fakeCartRepository.insert(PurchaseProduct(it.id, it, 1)) }

            viewModel = CartViewModel(fakeCartRepository)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.collect { }
            }

            assertEquals(5, viewModel.uiState.value.items.purchaseProducts.size)

            viewModel.next()
            assertEquals(2, viewModel.uiState.value.items.purchaseProducts.size)
        }

    @Test
    fun `마지막 페이지의 아이템을 모두 삭제하면 자동으로 이전 페이지로 이동한다`() =
        runTest {
            val products =
                (1..6).map {
                    Product(id = it.toLong(), name = "상품$it", price = 1000, imageUri = "uri", category = "카테고리")
                }
            products.forEach { fakeCartRepository.insert(PurchaseProduct(it.id, it, 1)) }

            viewModel = CartViewModel(fakeCartRepository)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.collect { }
            }

            viewModel.next()
            assertEquals(1, viewModel.uiState.value.currentPage)

            viewModel.removeWithID(6L)

            assertEquals(0, viewModel.uiState.value.currentPage)
        }

    @Test
    fun `수량 변경 시 최종 수량이 서버에 전달된다`() =
        runTest {
            val product = Product(id = 1L, name = "상품1", price = 1000, imageUri = "uri", category = "카테고리")
            fakeCartRepository.insert(PurchaseProduct(1L, product, 5))

            viewModel = CartViewModel(fakeCartRepository)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.collect { }
            }

            viewModel.updateCountWithID(1L, 1)

            assertEquals(
                6,
                viewModel.uiState.value.items.purchaseProducts[0]
                    .count,
            )

            viewModel.updateCountWithID(1L, -2)
            assertEquals(
                4,
                viewModel.uiState.value.items.purchaseProducts[0]
                    .count,
            )
        }
}
