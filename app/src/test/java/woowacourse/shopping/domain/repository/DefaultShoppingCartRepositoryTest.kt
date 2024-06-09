package woowacourse.shopping.domain.repository

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.remote.model.response.CartItemResponse
import woowacourse.shopping.remote.model.response.ProductResponse
import woowacourse.shopping.source.FakeShoppingCartDataSource
import woowacourse.shopping.ui.model.CartItem

class DefaultShoppingCartRepositoryTest {
    private lateinit var castSource: ShoppingCartDataSource
    private lateinit var repository: ShoppingCartRepository

    @BeforeEach
    fun setUp() {
        castSource = FakeShoppingCartDataSource()
        repository = DefaultShoppingCartRepository(castSource)
    }

    @Test
    fun `모든 장바구니 상품 불러오기 성공`() =
        runTest {
            // given
            castSource =
                FakeShoppingCartDataSource(
                    cartItemDtosTestFixture(10, cartItemFixture = { index -> cartItemDtoTestFixture(index, 1) }),
                )
            repository =
                DefaultShoppingCartRepository(
                    castSource,
                )

            // when
            val actual = repository.loadAllCartItems().getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                cartItemsTestFixture(10),
            )
        }

    @Test
    fun `장바구니에 있는 상품의 개수를 구한다`() =
        runTest {
            // given
            castSource =
                FakeShoppingCartDataSource(
                    cartItemDtosTestFixture(10) + cartItemDtoTestFixture(id = 10, quantity = 9),
                )

            repository =
                DefaultShoppingCartRepository(
                    castSource,
                )

            // when
            val actual = repository.shoppingCartProductQuantity().getOrThrow()

            // then
            assertThat(actual).isEqualTo(9)
        }

    @Test
    fun `장바구니에 있는 상품 중 id 에 해당하는 값의 수량을 업데이트한다`() =
        runTest {
            // given
            castSource =
                FakeShoppingCartDataSource(
                    cartItemDtosTestFixture(
                        10,
                        cartItemFixture = { index -> cartItemDtoTestFixture(index, 1) },
                    ),
                )
            repository =
                DefaultShoppingCartRepository(
                    castSource,
                )

            // when
            repository.updateProductQuantity(1, 10).getOrThrow()
            val actual =
                castSource.loadAllCartItems2().getOrThrow().find { it.id == 1L }?.quantity
                    ?: throw NoSuchElementException("there is no product")

            // then
            assertThat(actual).isEqualTo(10)
        }

    @Test
    fun `장바구니에 상품을 추가 담는다`() =
        runTest {
            // given
            castSource = FakeShoppingCartDataSource()
            repository = DefaultShoppingCartRepository(castSource)

            // when
            repository.addShoppingCartProduct(1, 5).getOrThrow()

            // then
            val actual =
                castSource.loadAllCartItems2().getOrThrow().find { it.id == 1L }?.quantity
                    ?: throw NoSuchElementException("there is no product")

            assertThat(actual).isEqualTo(5)
        }

    @Test
    fun `장바구니에 있는 상품 중 장바구니 id 값을 가진 상품을 제거한다`() =
        runTest {
            // given
            castSource = FakeShoppingCartDataSource(cartItemDtosTestFixture(10))
            repository = DefaultShoppingCartRepository(castSource)

            // when
            repository.removeShoppingCartProduct(1).getOrThrow()

            // then
            val actual = castSource.loadAllCartItems2().getOrThrow()
            assertThat(actual).hasSize(9)
        }
}

// test fixture for CartItemDto
fun cartItemDtoTestFixture(
    id: Int,
    quantity: Int = 0,
    product: ProductResponse = ProductResponse.DEFAULT,
): CartItemResponse =
    CartItemResponse(
        id = id.toLong(),
        quantity = quantity,
        product = product,
    )

fun cartItemDtosTestFixture(
    dataCount: Int,
    cartItemFixture: (Int) -> CartItemResponse = { cartItemDtoTestFixture(it) },
): List<CartItemResponse> =
    List(dataCount) { idx ->
        cartItemFixture(idx)
    }

/**
 * test fixture for CartItem
 * @param id: Long
 * @param quantity: Int
 * @param product: Product
 * @return CartItem
 */
fun cartItemTestFixture(
    id: Long = 1,
    quantity: Int = 1,
    product: Product = Product.DEFAULT,
): CartItem =
    CartItem(
        id = id,
        quantity = quantity,
        product = product,
        checked = false,
    )

fun cartItemsTestFixture(dataCount: Int): List<CartItem> =
    List(dataCount) { index ->
        cartItemTestFixture(
            id = index.toLong(),
        )
    }
