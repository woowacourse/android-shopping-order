package woowacourse.shopping.ui.cart

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.testing.fakes.FakeCartRepository
import woowacourse.shopping.testing.MainDispatcherExtension
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
    fun `현재 페이지 위치에 따라 페이지 이동 가능 여부가 결정되고 페이지를 이동할 수 있다`() = runTest {
        val products = (1..6).map {
            Product(id = it.toLong(), name = "상품$it", price = 1000, imageUri = "uri", category = "카테고리")
        }
        products.forEach { fakeCartRepository.insert(PurchaseProduct(it.id, it, 1)) }

        viewModel = CartViewModel(fakeCartRepository)
        testScheduler.advanceUntilIdle()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentPage.collect { }
        }

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.nextEnable.collect { }
        }

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.prevEnable.collect { }
        }

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.cartItemCount.collect { }
        }

        assertEquals(0, viewModel.currentPage.value)
        assertEquals(6, viewModel.cartItemCount.value)
        assertFalse(viewModel.prevEnable.value)
        assertTrue(viewModel.nextEnable.value)

        viewModel.next()
        assertEquals(1, viewModel.currentPage.value)
        assertTrue(viewModel.prevEnable.value)
        assertFalse(viewModel.nextEnable.value)

        viewModel.prev()
        assertEquals(0, viewModel.currentPage.value)
    }

    @Test
    fun `현재 페이지에 해당하는 상품만큼 서버에서 불러와 pagedCart를 구성한다`() = runTest {
        val products = (1..7).map {
            Product(id = it.toLong(), name = "상품$it", price = 1000, imageUri = "uri", category = "카테고리")
        }
        products.forEach { fakeCartRepository.insert(PurchaseProduct(it.id, it, 1)) }

        viewModel = CartViewModel(fakeCartRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.cartItemCount.collect { }
        }

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.pagedCart.collect { }
        }

        assertEquals(5, viewModel.pagedCart.value.purchaseProducts.size)

        viewModel.next()
        assertEquals(2, viewModel.pagedCart.value.purchaseProducts.size)
    }

    @Test
    fun `마지막 페이지의 아이템을 모두 삭제하면 자동으로 이전 페이지로 이동한다`() = runTest {
        val products = (1..6).map {
            Product(id = it.toLong(), name = "상품$it", price = 1000, imageUri = "uri", category = "카테고리")
        }
        products.forEach { fakeCartRepository.insert(PurchaseProduct(it.id, it, 1)) }

        viewModel = CartViewModel(fakeCartRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.cartItemCount.collect { }
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentPage.collect { }
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.pagedCart.collect { }
        }

        viewModel.next()
        assertEquals(1, viewModel.currentPage.value)

        viewModel.removeWithID(6L)

        assertEquals(0, viewModel.currentPage.value)
    }

    @Test
    fun `수량 변경 시 최종 수량이 서버에 전달된다`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.pagedCart.collect { }
        }

        val product = Product(id = 1L, name = "상품1", price = 1000, imageUri = "uri", category = "카테고리")
        fakeCartRepository.insert(PurchaseProduct(1L, product, 5))

        viewModel = CartViewModel(fakeCartRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.pagedCart.collect { }
        }

        viewModel.updateCountWithID(1L, 1)
        
        assertEquals(6, viewModel.pagedCart.value.purchaseProducts[0].count)

        viewModel.updateCountWithID(1L, -2)
        assertEquals(4, viewModel.pagedCart.value.purchaseProducts[0].count)
    }

    @Test
    fun `수량 변경 실패 시 에러 이벤트가 발생하고 로딩이 종료된다`() = runTest {
        val product = Product(id = 1L, name = "상품1", price = 1000, imageUri = "uri", category = "카테고리")
        fakeCartRepository.insert(PurchaseProduct(1L, product, 5))

        viewModel = CartViewModel(fakeCartRepository)
        testScheduler.advanceUntilIdle()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.isLoading.collect { }
        }

        fakeCartRepository.shouldFail = true
        val event = async(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.first() }

        viewModel.updateCountWithID(1L, 1)

        assertEquals(
            UiEvent.ShowMessage("수량 변경에 실패했습니다. 다시 시도해주세요."),
            event.await(),
        )

        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `아이템 삭제 실패 시 에러 이벤트가 발생하고 로딩이 종료된다`() = runTest {
        val product = Product(id = 1L, name = "상품1", price = 1000, imageUri = "uri", category = "카테고리")
        fakeCartRepository.insert(PurchaseProduct(1L, product, 5))

        viewModel = CartViewModel(fakeCartRepository)
        testScheduler.advanceUntilIdle()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.isLoading.collect { }
        }

        fakeCartRepository.shouldFail = true
        val event = async(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvent.first() }

        viewModel.removeWithID(1L)

        assertEquals(
            UiEvent.ShowMessage("아이템 삭제에 실패했습니다."),
            event.await(),
        )

        assertFalse(viewModel.isLoading.value)
    }
}
