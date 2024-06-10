package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.source.ShoppingCartDataSource

class DefaultOrderRepositoryTest {
    private lateinit var cartSource: ShoppingCartDataSource

//
//    @Test
//    fun `주문이 성공한다`() =
//        runTest {
//            // given
//            orderSource = FakeOrderDataSource()
//            cartSource = FakeShoppingCartDataSource()
//            repo = DefaultOrderRepository(orderSource, cartSource)
//            // when
//
//            val actual = repo.order(listOf(1, 2, 3))
//
//            // then
//            assertThat(actual.isSuccess).isTrue
//        }
//
//    @Test
//    fun `주문이 성공하면 저장되어있는 주문 아이템이 삭제된다`() =
//        runTest {
//            // given
//            orderSource = FakeOrderDataSource()
//            cartSource = FakeShoppingCartDataSource()
//
//            repo = DefaultOrderRepository(orderSource, cartSource)
//
//            // when
//            repo.order(listOf(1, 2, 3))
//
//            val actual = repo.orderItems().getOrThrow()
//
//            // then
//            assertThat(actual).isEmpty()
//        }
//
//    @Test
//    fun `주문 아이템에 저장한다`() =
//        runTest {
//            // given
//            orderSource = FakeOrderDataSource()
//            cartSource = FakeShoppingCartDataSource()
//
//            repo = DefaultOrderRepository(orderSource, cartSource)
//
//            // when
//            val actual = repo.saveOrderItem(1, 3)
//
//            // then
//            assertThat(actual.isSuccess).isTrue
//        }
//
//    @Test
//    fun `주문 아이템에 저장하면 해당 아이템을 불러올 수 있다 `() =
//        runTest {
//            // given
//            orderSource = FakeOrderDataSource()
//            cartSource = FakeShoppingCartDataSource()
//
//            repo = DefaultOrderRepository(orderSource, cartSource)
//
//            // when
//            repo.saveOrderItem(1, 3)
//            val actual = repo.orderItems().getOrThrow()
//
//            // then
//            assertThat(actual).isEqualTo(mapOf(1L to 3))
//        }
//
//    @Test
//    fun `주문 아이템들의 총 가격의 합을 구한다`() =
//        runTest {
//            // given
//            orderSource =
//                FakeOrderDataSource(
//                    orderSaved =
//                    mutableMapOf(
//                        101L to 3,
//                        102L to 2,
//                        103L to 1,
//                    ),
//                )
//
//            cartSource = FakeShoppingCartDataSource(
//                cartItemResponses =
//                listOf(
//                    CartItemResponse(
//                        101, 3, ProductResponse(
//                            101,
//                            "name",
//                            100,
//                            "1",
//                            "image",
//                        )
//                    ),
//                    CartItemResponse(
//                        102,
//                        2,
//                        ProductResponse(
//                            102,
//                            "name",
//                            200,
//                            "1",
//                            "image",
//                        ),
//                    ),
//                    CartItemResponse(
//                        103,
//                        1,
//                        ProductResponse(
//                            103,
//                            "name",
//                            300,
//                            "1",
//                            "image",
//                        ),
//                    ),
//                )
//            )
//
//            repo = DefaultOrderRepository(orderSource, cartSource)
//
//            // when
//            val actual = repo.orderItemsTotalPrice().getOrThrow()
//
//            // then
//            assertThat(actual).isEqualTo(1000)
//        }
}
