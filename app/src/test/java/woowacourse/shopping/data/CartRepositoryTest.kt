package woowacourse.shopping.data

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.dto.request.AddCartRequestBody
import woowacourse.shopping.data.remote.dto.request.UpdateCartRequestBody
import woowacourse.shopping.data.remote.dto.response.Pageable
import woowacourse.shopping.data.remote.dto.response.Sort
import woowacourse.shopping.data.remote.dto.response.cart.CartDto
import woowacourse.shopping.data.remote.dto.response.cart.CartProductDto
import woowacourse.shopping.data.remote.dto.response.cart.CartQuantityResponse
import woowacourse.shopping.data.remote.dto.response.cart.CartResponse
import woowacourse.shopping.data.repository.cart.CartRepositoryImpl

class CartRepositoryTest {
    @Test
    fun `장바구니 항목을 불러온다`() =
        runTest {
            val repository = CartRepositoryImpl(FakeCartApi(createCartDtos(size = 6)))

            repository.refreshCartItems()

            assertThat(repository.cartItems.value.map { it.id }).containsExactly("1", "2", "3", "4", "5", "6")
        }

    @Test
    fun `장바구니에 담긴 상품 수량을 반환한다`() =
        runTest {
            val repository = CartRepositoryImpl(FakeCartApi(listOf(createCartDto(id = 1, productId = 10, quantity = 3))))

            repository.refreshCartItems()
            val quantity = repository.getCartItemQuantity(productId = "10")

            assertThat(quantity).isEqualTo(3)
        }

    @Test
    fun `장바구니 상품 수량을 0으로 변경하면 삭제한다`() =
        runTest {
            val repository = CartRepositoryImpl(FakeCartApi(emptyList()))

            repository.refreshCartItems()
            val quantity = repository.getCartItemQuantity(productId = "10")

            assertThat(quantity).isNull()
        }

    @Test
    fun `장바구니 총 상품 수량을 반환한다`() =
        runTest {
            val repository =
                CartRepositoryImpl(
                    FakeCartApi(
                        listOf(
                            createCartDto(id = 1, productId = 1, quantity = 2),
                            createCartDto(id = 2, productId = 2, quantity = 3),
                        ),
                    ),
                )

            repository.refreshCartItems()
            repository.setCartItem(productId = "1", quantity = 5)

            assertThat(
                repository.cartItems.value
                    .first { it.product.id == "1" }
                    .quantity,
            ).isEqualTo(5)
        }

    @Test
    fun `장바구니에 없는 상품이면 새로 추가한다`() =
        runTest {
            val repository =
                CartRepositoryImpl(
                    FakeCartApi(
                        listOf(
                            createCartDto(id = 1, productId = 1, quantity = 2),
                            createCartDto(id = 2, productId = 2, quantity = 3),
                        ),
                    ),
                )

            repository.refreshCartItems()
            repository.setCartItem(productId = "3", quantity = 4)

            assertThat(
                repository.cartItems.value
                    .first { it.product.id == "3" }
                    .quantity,
            ).isEqualTo(4)
        }

    @Test
    fun `상품 삭제 후 상태를 갱신한다`() =
        runTest {
            val api = FakeCartApi(createCartDtos(size = 2))
            val repository = CartRepositoryImpl(api)

            repository.refreshCartItems()
            repository.deleteItem("1")

            assertThat(api.deletedIds).containsExactly(1L)
            assertThat(repository.cartItems.value.map { it.id }).containsExactly("2")
        }
}

private class FakeCartApi(
    private var items: List<CartDto>,
) : CartApi {
    val deletedIds = mutableListOf<Long>()

    override suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: List<String>,
    ): CartResponse {
        val fromIndex = page * size
        val pageItems = items.drop(fromIndex).take(size)
        return createCartResponse(
            content = pageItems,
            page = page,
            size = size,
            totalElements = items.size,
        )
    }

    override suspend fun addCartItem(addCartRequestBody: AddCartRequestBody) {
        val nextId = ((items.maxOfOrNull { it.id } ?: 0L) + 1)
        items =
            items +
            createCartDto(
                id = nextId,
                productId = addCartRequestBody.productId,
                quantity = addCartRequestBody.quantity,
            )
    }

    override suspend fun deleteCartItem(id: Long) {
        deletedIds += id
        items = items.filterNot { it.id == id }
    }

    override suspend fun updateCartItem(
        id: Long,
        updateCartRequestBody: UpdateCartRequestBody,
    ) {
        items =
            items.map { item ->
                if (item.id == id) {
                    item.copy(quantity = updateCartRequestBody.quantity)
                } else {
                    item
                }
            }
    }

    override suspend fun getCartItemsQuantity(): CartQuantityResponse = CartQuantityResponse(quantity = items.sumOf { it.quantity })
}

private fun createCartDtos(size: Int): List<CartDto> =
    (1..size).map { id ->
        createCartDto(id = id.toLong(), productId = id.toLong(), quantity = 1)
    }

private fun createCartDto(
    id: Long,
    productId: Long,
    quantity: Int,
    price: Long = 2000,
): CartDto =
    CartDto(
        id = id,
        product =
            CartProductDto(
                id = productId,
                name = "product$productId",
                price = price,
                imageUrl = "image$productId",
                category = "book",
            ),
        quantity = quantity,
    )

private fun createCartResponse(
    content: List<CartDto>,
    page: Int,
    size: Int,
    totalElements: Int,
): CartResponse {
    val totalPages =
        if (totalElements == 0) {
            1
        } else {
            ((totalElements - 1) / size) + 1
        }
    val last = page >= totalPages - 1
    val sort = Sort(empty = true, sorted = false, unsorted = true)

    return CartResponse(
        content = content,
        empty = content.isEmpty(),
        first = page == 0,
        last = last,
        number = page,
        numberOfElements = content.size,
        pageable =
            Pageable(
                offset = (page * size).toLong(),
                pageNumber = page,
                pageSize = size,
                paged = true,
                sort = sort,
                unpaged = false,
            ),
        size = size,
        sort = sort,
        totalElements = totalElements.toLong(),
        totalPages = totalPages,
    )
}
