package woowacourse.shopping.domain.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.remote.model.CartItemDto
import woowacourse.shopping.remote.model.ProductDto
import woowacourse.shopping.source.FakeShoppingCartDataSource

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
        castSource = FakeShoppingCartDataSource(
            cartItemDtosTestFixture(10)
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
        castSource = FakeShoppingCartDataSource(
            cartItemDtosTestFixture(10) + cartItemDtoTestFixture(id = 10, quantity = 9)
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
        castSource = FakeShoppingCartDataSource(
            cartItemDtosTestFixture(10)
        )
        repository = DefaultShoppingCartRepository(castSource)

        // when
        repository.updateProductQuantity(1, 10)

        // then
        val actual = castSource.loadAllCartItems().find { it.id == 1L }?.quantity
            ?: throw NoSuchElementException("there is no product")
        assertThat(actual).isEqualTo(10)
    }
}


// test fixture for CartItemDto
fun cartItemDtoTestFixture(id: Int, quantity: Int = 1, product: ProductDto = ProductDto.DEFAULT): CartItemDto =
    CartItemDto(
        id = id.toLong(),
        quantity = quantity,
        product = product,
    )

fun cartItemDtosTestFixture(dataCount: Int): List<CartItemDto> = List(dataCount) {
    cartItemDtoTestFixture(
        id = it + 1
    )
}