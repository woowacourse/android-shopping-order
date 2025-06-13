package woowacourse.shopping.fixture

import woowacourse.shopping.data.remote.cart.CartResponse

object CartResponseFixture {
    fun createCartResponse(): CartResponse =
        CartResponse(
            content =
                listOf(
                    CartResponse.Content(
                        id = 1L,
                        quantity = 1,
                        product =
                            CartResponse.Content.CartRemoteProduct(
                                id = 100L,
                                name = "테스트 상품",
                                price = 1000,
                                imageUrl = "https://example.com/test.jpg",
                                category = "음식",
                            ),
                    ),
                ),
            empty = false,
            first = true,
            last = true,
            number = 0,
            numberOfElements = 1,
            pageable =
                CartResponse.Pageable(
                    offset = 0,
                    pageNumber = 0,
                    pageSize = 20,
                    paged = true,
                    unpaged = false,
                    sort =
                        CartResponse.Sort(
                            empty = false,
                            sorted = true,
                            unsorted = false,
                        ),
                ),
            size = 20,
            sort =
                CartResponse.Sort(
                    empty = false,
                    sorted = true,
                    unsorted = false,
                ),
            totalElements = 10,
            totalPages = 1,
        )
}
