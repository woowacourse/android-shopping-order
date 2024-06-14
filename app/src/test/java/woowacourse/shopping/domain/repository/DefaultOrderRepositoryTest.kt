package woowacourse.shopping.domain.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.model.OrderItemData
import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.model.Orders
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.remote.model.response.CartItemResponse
import woowacourse.shopping.remote.model.response.ProductResponse
import woowacourse.shopping.source.FakeShoppingCartDataSource
import woowacourse.woowacourse.shopping.source.FakeOrderDataSource
import java.time.LocalDateTime

class DefaultOrderRepositoryTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
    private lateinit var cartSource: ShoppingCartDataSource
    private lateinit var orderSource: OrderDataSource

    private lateinit var repo: OrderRepository

    @BeforeEach
    fun setUp() {
        cartSource =
            FakeShoppingCartDataSource(
                cartItemResponses = listOf(),
                dispatcher = dispatcher,
            )

        orderSource =
            FakeOrderDataSource(
                orderItems = mutableListOf(),
                dispatcher = dispatcher,
            )
        repo =
            DefaultOrderRepository(
                orderSource = orderSource,
                cartSource = cartSource,
                orderDateTime = FakeOrderDateTime(),
            )
    }

    @Test
    fun `주문 아이템을 하나 저장한다`() =
        runTest {
            // given setup empty cartSource and orderSource

            // when
            val actual =
                repo.save(
                    OrderItem(
                        cartItemId = 1,
                        quantity = 3,
                        product = productDomainTestFixture(1),
                    ),
                )

            // then
            assertThat(actual.isSuccess).isTrue
        }

    @Test
    fun `모든 주문을 불러온다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = { index -> cartItemDtoTestFixture(index, 1) },
                        ),
                    dispatcher = dispatcher,
                )
            orderSource =
                FakeOrderDataSource(
                    orderItems = orderItemsDataTestFixture(dataCount = 10).toMutableList(),
                    dispatcher = dispatcher,
                )

            repo =
                DefaultOrderRepository(
                    orderSource = orderSource,
                    cartSource = cartSource,
                    orderDateTime = FakeOrderDateTime(),
                )

            // when
            val actual = repo.loadAllOrders().getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                Orders(
                    orderItems = orderItemsDataTestFixture(10).toDomain(),
                    totalPrice = 0,
                    orderDateTime = LocalDateTime.of(2021, 1, 1, 0, 0),
                ),
            )
        }

    @Test
    fun `주문을 저장하고 불러온다`() =
        runTest {
            // given setup empty cartSource and orderSource

            // when
            repo.save(
                OrderItem(
                    cartItemId = 1,
                    quantity = 3,
                    product = productDomainTestFixture(1),
                ),
            )

            val actual = repo.loadAllOrders().getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                Orders(
                    orderItems =
                        listOf(
                            OrderItem(
                                cartItemId = 1,
                                quantity = 3,
                                product = productDomainTestFixture(id = 1),
                            ),
                        ),
                    totalPrice = 3,
                    orderDateTime = LocalDateTime.of(2021, 1, 1, 0, 0),
                ),
            )
        }

    @Test
    fun `주문 아이템들을 저장한다`() =
        runTest {
            // given setup empty cartSource and orderSource

            // when
            val actual =
                repo.save(
                    orderItemsDataTestFixture(3).toDomain(),
                )

            // then
            assertThat(actual.isSuccess).isTrue
        }

    @Test
    fun `주문 아이템들을 저장하고 불러온다`() =
        runTest {
            // given setup empty cartSource and orderSource

            // when
            repo.save(
                orderItemsDataTestFixture(3).toDomain(),
            )

            val actual = repo.loadAllOrders().getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                Orders(
                    orderItems = orderItemsDataTestFixture(3).toDomain(),
                    totalPrice = 0,
                    orderDateTime = LocalDateTime.of(2021, 1, 1, 0, 0),
                ),
            )
        }

    @Test
    fun `주문 아이템들의 총 가격의 합을 구한다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = { index -> cartItemDtoTestFixture(index, 1) },
                        ),
                    dispatcher = dispatcher,
                )
            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        orderItemsDataTestFixture(
                            dataCount = 10,
                            orderItemFixture = { orderItemDataTestFixture(it.toLong(), 1) },
                        ).toMutableList(),
                    dispatcher = dispatcher,
                )

            repo =
                DefaultOrderRepository(
                    orderSource = orderSource,
                    cartSource = cartSource,
                    orderDateTime = FakeOrderDateTime(),
                )

            // when
            val actual = repo.orderItemsTotalPrice().getOrThrow()

            // then
            assertThat(actual).isEqualTo(0)
        }

    @Test
    fun `상품 id 를 가진 주문 아이템의 개수를 업데이트한다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        listOf(
                            CartItemResponse(
                                id = 1,
                                quantity = 1,
                                product =
                                    ProductResponse(
                                        id = 1,
                                        name = "name",
                                        price = 100,
                                        category = "1",
                                        imageUrl = "image",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )

            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        mutableListOf(
                            OrderItemData(
                                cartItemId = 101, quantity = 1,
                                product =
                                    ProductData(
                                        id = 1,
                                        name = "name",
                                        imgUrl = "image",
                                        price = 100,
                                        category = "1",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )
            repo =
                DefaultOrderRepository(
                    orderSource = orderSource,
                    cartSource = cartSource,
                    orderDateTime = FakeOrderDateTime(),
                )

            // when
            val actual = repo.updateOrderItem(1, 3)

            // then
            assertThat(actual.isSuccess).isTrue
        }

    @Test
    fun `상품 id 를 가진 주문 아이템의 개수를 업데이트하고 로드해서 확인한다`() =
        runTest {
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        listOf(
                            CartItemResponse(
                                id = 101,
                                quantity = 1,
                                product =
                                    ProductResponse(
                                        id = 1,
                                        name = "name",
                                        price = 100,
                                        category = "1",
                                        imageUrl = "image",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )

            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        mutableListOf(
                            OrderItemData(
                                cartItemId = 101, quantity = 1,
                                product =
                                    ProductData(
                                        id = 1,
                                        name = "name",
                                        imgUrl = "image",
                                        price = 100,
                                        category = "1",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )
            repo =
                DefaultOrderRepository(
                    orderSource = orderSource,
                    cartSource = cartSource,
                    orderDateTime = FakeOrderDateTime(),
                )

            // when
            repo.updateOrderItem(1, 3)
            val actual = repo.loadAllOrders().getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                Orders(
                    orderItems =
                        listOf(
                            OrderItem(
                                cartItemId = 101,
                                quantity = 3,
                                product =
                                    Product(
                                        id = 1L,
                                        imgUrl = "image",
                                        name = "name",
                                        price = 100,
                                        quantity = 0,
                                        category = "1",
                                    ),
                            ),
                        ),
                    totalPrice = 300,
                    orderDateTime = LocalDateTime.of(2021, 1, 1, 0, 0),
                ),
            )
        }

    @Test
    fun `주문을 한다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        listOf(
                            CartItemResponse(
                                id = 101,
                                quantity = 1,
                                product =
                                    ProductResponse(
                                        id = 1,
                                        name = "name",
                                        price = 100,
                                        category = "1",
                                        imageUrl = "image",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )
            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        mutableListOf(
                            OrderItemData(
                                cartItemId = 101, quantity = 1,
                                product =
                                    ProductData(
                                        id = 1,
                                        name = "name",
                                        imgUrl = "image",
                                        price = 100,
                                        category = "1",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )
            repo =
                DefaultOrderRepository(
                    orderSource = orderSource,
                    cartSource = cartSource,
                    orderDateTime = FakeOrderDateTime(),
                )

            // when
            val actual = repo.order()

            // then
            assertThat(actual.isSuccess).isTrue
        }

    @Test
    fun `주문 item 의 모든 개수를 구한다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        listOf(
                            CartItemResponse(
                                id = 101,
                                quantity = 1,
                                product =
                                    ProductResponse(
                                        id = 1,
                                        name = "name",
                                        price = 100,
                                        category = "1",
                                        imageUrl = "image",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )
            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        mutableListOf(
                            OrderItemData(
                                cartItemId = 101, quantity = 1,
                                product =
                                    ProductData(
                                        id = 1,
                                        name = "name",
                                        imgUrl = "image",
                                        price = 100,
                                        category = "1",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )
            repo =
                DefaultOrderRepository(
                    orderSource = orderSource,
                    cartSource = cartSource,
                    orderDateTime = FakeOrderDateTime(),
                )

            // when
            val actual = repo.allOrderItemsQuantity().getOrThrow()

            // then
            assertThat(actual).isEqualTo(1)
        }

    @Test
    fun `모든 주문 아이템의 총 가격 (각 개수와 가격의 곱의 합계 ) 을 구한다`() =
        runTest {
            // given
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        listOf(
                            CartItemResponse(
                                id = 101,
                                quantity = 1,
                                product =
                                    ProductResponse(
                                        id = 1,
                                        name = "name",
                                        price = 100,
                                        category = "1",
                                        imageUrl = "image",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )
            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        mutableListOf(
                            OrderItemData(
                                cartItemId = 101, quantity = 1,
                                product =
                                    ProductData(
                                        id = 1,
                                        name = "name",
                                        imgUrl = "image",
                                        price = 100,
                                        category = "1",
                                    ),
                            ),
                        ),
                    dispatcher = dispatcher,
                )
            repo =
                DefaultOrderRepository(
                    orderSource = orderSource,
                    cartSource = cartSource,
                    orderDateTime = FakeOrderDateTime(),
                )

            // when
            val actual = repo.orderItemsTotalPrice().getOrThrow()

            // then
            assertThat(actual).isEqualTo(100)
        }
}

// test fixture for orderItem

fun orderItemsDataTestFixture(
    dataCount: Int,
    orderItemFixture: (Int) -> OrderItemData = { orderItemDataTestFixture(it.toLong()) },
): List<OrderItemData> = List(dataCount) { index -> orderItemFixture(index) }

fun orderItemDataTestFixture(
    id: Long,
    quantity: Int = 1,
    product: ProductData = ProductData.DEFAULT,
): OrderItemData =
    OrderItemData(
        cartItemId = id,
        quantity = quantity,
        product = product,
    )
