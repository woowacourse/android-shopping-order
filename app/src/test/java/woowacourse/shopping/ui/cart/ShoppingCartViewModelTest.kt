package woowacourse.shopping.ui.cart

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.cartItemDtoTestFixture
import woowacourse.shopping.domain.repository.cartItemDtosTestFixture
import woowacourse.shopping.domain.repository.productResponseTestFixture
import woowacourse.shopping.source.FakeShoppingCartDataSource
import woowacourse.shopping.ui.cart.event.ShoppingCartError
import woowacourse.shopping.ui.cart.event.ShoppingCartEvent
import woowacourse.shopping.ui.model.CartItem
import woowacourse.woowacourse.shopping.source.FakeOrderDataSource

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ShoppingCartViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()

    private lateinit var cartSource: ShoppingCartDataSource
    private lateinit var orderSource: OrderDataSource

    private lateinit var cartRepository: ShoppingCartRepository
    private lateinit var orderRepository: OrderRepository

    private lateinit var viewModel: ShoppingCartViewModel

    @BeforeEach
    fun setUp() {
        cartSource = FakeShoppingCartDataSource(dispatcher = dispatcher)
        orderSource = FakeOrderDataSource(dispatcher = dispatcher)

        cartRepository = DefaultShoppingCartRepository(cartSource)
        orderRepository = DefaultOrderRepository(orderSource, cartSource)

        viewModel = ShoppingCartViewModel(cartRepository, orderRepository)
    }

    @Test
    fun `모든 카트 아이템을 로드하면 ui 상태로 장바구니 아이템이 업데이트된다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = { index ->
                                cartItemDtoTestFixture(
                                    index,
                                    1,
                                    productResponseTestFixture(index.toLong()),
                                )
                            },
                        ),
                    dispatcher = dispatcher,
                )

            cartRepository = DefaultShoppingCartRepository(cartSource)
            orderRepository = DefaultOrderRepository(orderSource, cartSource)

            viewModel = ShoppingCartViewModel(cartRepository, orderRepository)

            // when
            viewModel.loadAll()

            // then
            val cartItems = viewModel.cartItems.value.orEmpty()
            assertThat(cartItems.size).isEqualTo(10)
        }

    @Test
    fun `장바구니 상품 id 에 해당하는 장바구니 상품을 제거하면 장바구니에 있는 상품의 총 개수가 감소한다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = { index ->
                                cartItemDtoTestFixture(
                                    index,
                                    1,
                                    productResponseTestFixture(index.toLong()),
                                )
                            },
                        ),
                    dispatcher = dispatcher,
                )
            orderSource = FakeOrderDataSource(dispatcher = dispatcher)

            cartRepository = DefaultShoppingCartRepository(cartSource)
            orderRepository = DefaultOrderRepository(orderSource, cartSource)

            viewModel = ShoppingCartViewModel(cartRepository, orderRepository)
            viewModel.loadAll()
            // when
            viewModel.deleteItem(1)

            // then
            val actual = viewModel.cartItems.value.orEmpty()
            assertThat(actual.size).isEqualTo(9)
        }

    @Test
    fun `장바구니 상품의 개수를 증가시킨다 `() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            3,
                            cartItemFixture = { index ->
                                cartItemDtoTestFixture(
                                    index,
                                    1,
                                    productResponseTestFixture(index.toLong()),
                                )
                            },
                        ),
                    dispatcher = dispatcher,
                )

            cartRepository = DefaultShoppingCartRepository(cartSource)
            orderRepository = DefaultOrderRepository(orderSource, cartSource)

            viewModel = ShoppingCartViewModel(cartRepository, orderRepository)
            viewModel.loadAll()

            // when
            viewModel.onIncrease(productId = 1, quantity = 2)

            // then
            val actual = viewModel.cartItems.value.orEmpty().find { it.id == 1L }
            assertThat(actual?.quantity).isEqualTo(2)
        }

    @Test
    fun `장바구니 상품의 개수를 줄인다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = { index ->
                                cartItemDtoTestFixture(
                                    index,
                                    3,
                                    productResponseTestFixture(index.toLong()),
                                )
                            },
                        ),
                    dispatcher = dispatcher,
                )

            cartRepository = DefaultShoppingCartRepository(cartSource)
            orderRepository = DefaultOrderRepository(orderSource, cartSource)

            viewModel = ShoppingCartViewModel(cartRepository, orderRepository)
            viewModel.loadAll()

            // when
            viewModel.onDecrease(productId = 1, quantity = 2)

            // then
            val actual = viewModel.cartItems.value.orEmpty().find { it.id == 1L }
            assertThat(actual?.quantity).isEqualTo(2)
        }

    @Test
    fun `장바구니 상품 하나를 선택된 상태로 변경한다 `() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = { index ->
                                cartItemDtoTestFixture(
                                    index,
                                    3,
                                    productResponseTestFixture(index.toLong()),
                                )
                            },
                        ),
                    dispatcher = dispatcher,
                )

            cartRepository = DefaultShoppingCartRepository(cartSource)
            orderRepository = DefaultOrderRepository(orderSource, cartSource)

            viewModel = ShoppingCartViewModel(cartRepository, orderRepository)
            viewModel.loadAll()

            // when
            viewModel.selected(cartItemId = 1)

            // then
            val actual = viewModel.cartItems.value.orEmpty().find { it.id == 1L }
            assertThat(actual?.checked).isTrue()
        }

    @Test
    fun `모두 선택 버튼을 누르면 모든 장바구니 상품을 모두 선택한 상태로 변경한다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = { index ->
                                cartItemDtoTestFixture(
                                    index,
                                    3,
                                    productResponseTestFixture(index.toLong()),
                                )
                            },
                        ),
                    dispatcher = dispatcher,
                )

            cartRepository = DefaultShoppingCartRepository(cartSource)
            orderRepository = DefaultOrderRepository(orderSource, cartSource)

            viewModel = ShoppingCartViewModel(cartRepository, orderRepository)
            viewModel.loadAll()

            // when
            viewModel.selectedAll()

            // then
            val actual = viewModel.cartItems.value.orEmpty()
            assertThat(actual.all(CartItem::checked)).isTrue
        }

    @Test
    fun `모든 상품을 선택한 후 하나의 상품을 제거해도 남아 있는 선택된 샆움의 개수와 가격을 보여준다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            3,
                            cartItemFixture = { index ->
                                cartItemDtoTestFixture(
                                    id = index,
                                    quantity = 3,
                                    productResponseTestFixture(index.toLong()),
                                )
                            },
                        ),
                    dispatcher = dispatcher,
                )
            cartRepository = DefaultShoppingCartRepository(cartSource)
            orderRepository = DefaultOrderRepository(orderSource, cartSource)

            viewModel = ShoppingCartViewModel(cartRepository, orderRepository)

            viewModel.loadAll()
            viewModel.selectedAll()

            // when
            viewModel.deleteItem(1)

            // then
            val actualSelectedItemsCount = viewModel.selectedCartItemsCount.value
            assertThat(actualSelectedItemsCount).isEqualTo(6)

            val actualSelectedItemsTotalPrice = viewModel.selectedCartItemsTotalPrice.value
            assertThat(actualSelectedItemsTotalPrice).isEqualTo(6000)
        }

    @Test
    fun `성택된 상품이 없을 때 주문하려고 하면 비어 있는 주문 에러이다`() =
        runTest {
            // given setup empty

            // when
            viewModel.navigateToOrder()

            // then
            val actual = viewModel.error.getValue()
            assertThat(actual).isEqualTo(ShoppingCartError.EmptyOrderProduct)
        }

    @Test
    fun `장바구니에 없는 상품을 제거하려고 할 때 error 가 설정된다`() =
        runTest {
            // given setup empty

            // when
            viewModel.deleteItem(cartItemId = 101)

            // then
            val actual = viewModel.error.getValue()
            assertThat(actual).isEqualTo(ShoppingCartError.DeleteCartItem)
        }

    @Test
    fun `주문하기 화면으로 넘어가면 order 저장소에 저장된다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = { index ->
                                cartItemDtoTestFixture(
                                    index,
                                    3,
                                    productResponseTestFixture(index.toLong()),
                                )
                            },
                        ),
                    dispatcher = dispatcher,
                )

            cartRepository = DefaultShoppingCartRepository(cartSource)
            orderRepository = DefaultOrderRepository(orderSource, cartSource)

            viewModel = ShoppingCartViewModel(cartRepository, orderRepository)
            viewModel.loadAll()
            viewModel.selectedAll()

            // when
            viewModel.navigateToOrder()

            // then
            val actualEvent = viewModel.event.getValue()
            assertThat(actualEvent).isEqualTo(ShoppingCartEvent.NavigationOrder)

            val actualOrders = orderRepository.loadAllOrders().getOrThrow()
            assertThat(actualOrders.orderItems.size).isEqualTo(10)
        }
}

@ExperimentalCoroutinesApi
class CoroutinesTestExtension(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : BeforeEachCallback, AfterEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(dispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}
