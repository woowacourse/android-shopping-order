package woowacourse.shopping.domain.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.remote.model.response.CartItemResponse
import woowacourse.shopping.remote.model.response.ProductResponse
import woowacourse.shopping.source.FakeShoppingCartDataSource
import woowacourse.shopping.ui.model.CartItem
import woowacourse.woowacourse.shopping.testfixture.CoroutinesTestExtension

class DefaultShoppingCartRepositoryTest {
    private lateinit var castSource: ShoppingCartDataSource
    private lateinit var repository: ShoppingCartRepository

    @BeforeEach
    fun setUp() {
        castSource = FakeShoppingCartDataSource()
        repository = DefaultShoppingCartRepository(castSource)
    }

    @Test
    fun `모든 장바구니 상품을 불러온다`() {
        // given
        castSource =
            FakeShoppingCartDataSource(
                cartItemDtosTestFixture(10),
            )
        repository = DefaultShoppingCartRepository(castSource)

        // when
        val actual = repository.loadAllCartItems()

        // then
        assertThat(actual).hasSize(10)
    }

    @Test
    fun `장바구니 상품의 총 개수를 계산한다`() {
        // given
        castSource =
            FakeShoppingCartDataSource(
                cartItemDtosTestFixture(10) + cartItemDtoTestFixture(id = 10, quantity = 9),
            )
        repository = DefaultShoppingCartRepository(castSource)

        // when
        val actual = repository.shoppingCartProductQuantity()

        // then
        assertThat(actual).isEqualTo(19)
    }

    @Test
    fun `상품의 개수를 변경한다`() {
        // given
        castSource =
            FakeShoppingCartDataSource(
                cartItemDtosTestFixture(10),
            )
        repository = DefaultShoppingCartRepository(castSource)

        // when
        repository.updateProductQuantity(1, 10)

        // then
        val actual =
            castSource.loadAllCartItems().find { it.id == 1L }?.quantity
                ?: throw NoSuchElementException("there is no product")
        assertThat(actual).isEqualTo(10)
    }

    // test suspend function

    @Test
    fun `모든 장바구니 상품 불러오기 성공`() =
        runTest {
            // given
            castSource =
                FakeShoppingCartDataSource(
                    cartItemDtosTestFixture(10),
                )
            repository =
                DefaultShoppingCartRepository(
                    castSource,
                )

            // when
            val actual = repository.loadAllCartItems2().getOrThrow()

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
            val actual = repository.shoppingCartProductQuantity2().getOrThrow()

            // then
            assertThat(actual).isEqualTo(19)
        }

    @Test
    fun `장바구니에 있는 상품 중 id 에 해당하는 값의 수량을 업데이트한다`() =
        runTest {
            // given
            castSource =
                FakeShoppingCartDataSource(
                    cartItemDtosTestFixture(10),
                )
            repository =
                DefaultShoppingCartRepository(
                    castSource,
                )

            // when
            repository.updateProductQuantity2(1, 10).getOrThrow()
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
            repository.addShoppingCartProduct2(1, 5).getOrThrow()

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
            repository.removeShoppingCartProduct2(1).getOrThrow()

            // then
            val actual = castSource.loadAllCartItems2().getOrThrow()
            assertThat(actual).hasSize(9)
        }
}

// test fixture for CartItemDto
fun cartItemDtoTestFixture(
    id: Int,
    quantity: Int = 1,
    product: ProductResponse = ProductResponse.DEFAULT,
): CartItemResponse =
    CartItemResponse(
        id = id.toLong(),
        quantity = quantity,
        product = product,
    )

fun cartItemDtosTestFixture(dataCount: Int): List<CartItemResponse> =
    List(dataCount) {
        cartItemDtoTestFixture(
            id = it + 1,
        )
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
    List(dataCount) {
        cartItemTestFixture(
            id = it + 1L,
        )
    }

fun cartItemDataTestFixture(
    id: Long = 1,
    quantity: Int = 1,
    product: ProductData = ProductData.NULL,
): CartItemData =
    CartItemData(
        id = id,
        quantity = quantity,
        product = product,
    )

fun cartItemsDataTestFixture(dataCount: Int): List<CartItemData> =
    List(dataCount) {
        cartItemDataTestFixture(
            id = it + 1L,
        )
    }
