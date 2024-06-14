package woowacourse.shopping.ui.order

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.model.HistoryProduct
import woowacourse.shopping.data.model.OrderItemData
import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.repository.CategoryBasedProductRecommendationRepository
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.FakeOrderDateTime
import woowacourse.shopping.domain.repository.OrderDateTime
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductsRecommendationRepository
import woowacourse.shopping.domain.repository.orderItemsDataTestFixture
import woowacourse.shopping.productTestFixture
import woowacourse.shopping.productsTestFixture
import woowacourse.shopping.source.FakeProductDataSource
import woowacourse.shopping.source.FakeProductHistorySource
import woowacourse.shopping.source.FakeShoppingCartDataSource
import woowacourse.shopping.ui.cart.CoroutinesTestExtension
import woowacourse.woowacourse.shopping.source.FakeOrderDataSource

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class OrderViewModelTest {
    private lateinit var orderSource: OrderDataSource
    private lateinit var cartSource: ShoppingCartDataSource
    private lateinit var orderDateTime: OrderDateTime
    private lateinit var productSource: ProductDataSource
    private lateinit var historySource: ProductHistoryDataSource

    private lateinit var orderRepository: OrderRepository
    private lateinit var productsRecommendationRepository: ProductsRecommendationRepository

    private lateinit var viewModel: OrderViewModel

    @BeforeEach
    fun setUp() {
        orderSource = FakeOrderDataSource()
        cartSource = FakeShoppingCartDataSource()
        orderDateTime = FakeOrderDateTime()
        historySource = FakeProductHistorySource()
        productSource =
            FakeProductDataSource(
                allProducts = productsTestFixture(60).toMutableList(),
            )

        orderRepository = DefaultOrderRepository(orderSource, cartSource, orderDateTime)
        productsRecommendationRepository =
            CategoryBasedProductRecommendationRepository(
                productSource,
                cartSource,
                historySource,
            )
        viewModel = OrderViewModel(orderRepository, productsRecommendationRepository)
    }

    @Test
    fun `뷰모델은 데이터를 불러올 때 추천 상품들을 불러온다`() =
        runTest {
            // given
            productSource =
                FakeProductDataSource(
                    allProducts =
                        mutableListOf(
                            ProductData(1, "", "", 1000, "fashion"),
                            ProductData(2, "", "", 2000, "fashion"),
                            ProductData(3, "", "", 3000, "fashion"),
                            ProductData(4, "", "", 4000, "fashion"),
                            ProductData(5, "", "", 5000, "fashion"),
                            ProductData(6, "", "", 6000, "fashion"),
                            ProductData(7, "", "", 5000, "electronics"),
                            ProductData(8, "", "", 6000, "electronics"),
                        ),
                )

            historySource =
                FakeProductHistorySource(
                    history =
                        mutableListOf(
                            HistoryProduct(1),
                            HistoryProduct(2),
                            HistoryProduct(3),
                        ),
                )
            productsRecommendationRepository =
                CategoryBasedProductRecommendationRepository(productSource, cartSource, historySource)
            orderRepository = DefaultOrderRepository(orderSource, cartSource, orderDateTime)

            viewModel = OrderViewModel(orderRepository, productsRecommendationRepository)

            // when
            viewModel.loadAll()

            // then
            val actual = viewModel.recommendedProducts.getValue().orEmpty()

            assertThat(actual).isEqualTo(
                listOf(
                    ProductData(1, "", "", 1000, "fashion").toDomain(),
                    ProductData(2, "", "", 2000, "fashion").toDomain(),
                    ProductData(3, "", "", 3000, "fashion").toDomain(),
                    ProductData(4, "", "", 4000, "fashion").toDomain(),
                    ProductData(5, "", "", 5000, "fashion").toDomain(),
                    ProductData(6, "", "", 6000, "fashion").toDomain(),
                ),
            )
        }

    @Test
    fun `뷰모델이 데이터를 불러올 때 주문 상품의 총 가격을 계산한다`() =
        runTest {
            // given
            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        orderItemsDataTestFixture(
                            dataCount = 3,
                            orderItemFixture = { id ->
                                OrderItemData(id.toLong(), 1, productTestFixture(id = id.toLong(), price = 1000 * id))
                            },
                        ).toMutableList(),
                )
            orderRepository = DefaultOrderRepository(orderSource, cartSource, orderDateTime)

            viewModel = OrderViewModel(orderRepository, productsRecommendationRepository)

            // when
            viewModel.loadAll()

            // then
            val actual = viewModel.totalPrice.value
            assertThat(actual).isEqualTo(3000)
        }

    @Test
    fun `뷰모델이 데이터를 불러올 때 주문 상품의 총 개수를 센다`() =
        runTest {
            // given
            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        orderItemsDataTestFixture(
                            dataCount = 3,
                            orderItemFixture = { id ->
                                OrderItemData(id.toLong(), 2, productTestFixture(id = id.toLong(), price = 1000 * id))
                            },
                        ).toMutableList(),
                )
            orderRepository = DefaultOrderRepository(orderSource, cartSource, orderDateTime)

            viewModel = OrderViewModel(orderRepository, productsRecommendationRepository)

            // when
            viewModel.loadAll()

            // then
            val actual = viewModel.addedProductQuantity.value
            assertThat(actual).isEqualTo(6)
        }

    @Test
    fun `추천 상품을 추가하면 주문 상품의 총 개수가 1 증가한다`() =
        runTest {
            // given
            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        orderItemsDataTestFixture(
                            dataCount = 3,
                            orderItemFixture = { id ->
                                OrderItemData(id.toLong(), 2, productTestFixture(id = id.toLong(), price = 1000 * id))
                            },
                        ).toMutableList(),
                )
            orderRepository = DefaultOrderRepository(orderSource, cartSource, orderDateTime)

            viewModel = OrderViewModel(orderRepository, productsRecommendationRepository)
            viewModel.loadAll()

            // when
            viewModel.onIncrease(1, 2)

            // then
            val actual = viewModel.addedProductQuantity.value
            assertThat(actual).isEqualTo(7)
        }

    @Test
    fun `추천 상품을 추가하면 주문 상품의 총 가격의 합도 변경된다`() =
        runTest {
            // given
            orderSource =
                FakeOrderDataSource(
                    orderItems =
                        orderItemsDataTestFixture(
                            dataCount = 3,
                            orderItemFixture = { id ->
                                OrderItemData(id.toLong(), 2, productTestFixture(id = id.toLong(), price = 1000 * id))
                            },
                        ).toMutableList(),
                )
            orderRepository = DefaultOrderRepository(orderSource, cartSource, orderDateTime)

            viewModel = OrderViewModel(orderRepository, productsRecommendationRepository)
            viewModel.loadAll()

            println(viewModel.totalPrice.value)

            // when
            viewModel.onIncrease(1, 1)

            // then
            val actual = viewModel.totalPrice.value
            assertThat(actual).isEqualTo(6001)
        }

//    @Test
//    fun `추천 상품을 불러온다 장바구니에 아무것도 안들어 있는 케이스`() {
//        // given
//        productsSource =
//            FakeProductDataSource(
//                allProducts =
//                    productsTestFixture(60) { id ->
//                        productTestFixture(id = id.toLong(), name = "$id name", price = 1, imageUrl = "1", category = "fashion")
//                    }.toMutableList(),
//            )
//        orderRepository = DefaultOrderRepository(FakeOrderDataSource(), productsSource)
//        historyRepository =
//            DefaultProductHistoryRepository(
//                productHistoryDataSource =
//                    FakeProductHistorySource(
//                        history = mutableListOf(1, 2, 3),
//                    ),
//                productDataSource = productsSource,
//            )
//
//        cartRepository =
//            DefaultShoppingCartRepository(
//                FakeShoppingCartDataSource(
//                    cartItemResponses = listOf(),
//                ),
//            )
//
//        productRecommendationRepository =
//            CategoryBasedProductRecommendationRepository(
//                productsSource,
//                FakeShoppingCartDataSource(
//                    cartItemResponses = mutableListOf(),
//                ),
//            )
//
//        viewModel =
//            OrderViewModel(
//                orderRepository = orderRepository,
//                historyRepository = historyRepository,
//                productsRecommendationRepository = productRecommendationRepository,
//                cartRepository = cartRepository,
//            )
//
//        // when
//        viewModel.loadAll()
//
//        // then
//        val actual = viewModel.recommendedProducts.getOrAwaitValue()
//        assertThat(actual).isEqualTo(
//            productsTestFixture(10).map {
//                ProductData(
//                    id = it.id,
//                    name = it.name,
//                    price = it.price,
//                    category = "fashion",
//                    imgUrl = it.imgUrl,
//                ).toDomain(0)
//            },
//        )
//    }
}
