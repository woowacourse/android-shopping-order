package woowacourse.shopping.ui.order

import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductsRecommendationRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository

@ExtendWith(InstantTaskExecutorExtension::class)
class OrderViewModelTest {
    private lateinit var productsSource: ProductDataSource

    private lateinit var historyRepository: ProductHistoryRepository
    private lateinit var productRecommendationRepository: ProductsRecommendationRepository
    private lateinit var cartRepository: ShoppingCartRepository

    private lateinit var viewModel: OrderViewModel

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
