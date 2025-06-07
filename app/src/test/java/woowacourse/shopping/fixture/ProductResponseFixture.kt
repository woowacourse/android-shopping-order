package woowacourse.shopping.fixture

import woowacourse.shopping.data.remote.product.ProductResponse

object ProductResponseFixture {
    fun createProductResponse(): ProductResponse =
        ProductResponse(
            content =
                listOf(
                    ProductResponse.Content(
                        id = 1L,
                        name = "테스트 상품",
                        price = 12000,
                        imageUrl = "https://example.com/product.jpg",
                        category = "테스트카테고리",
                    ),
                ),
            empty = false,
            first = true,
            last = true,
            number = 0,
            numberOfElements = 1,
            pageable =
                ProductResponse.Pageable(
                    offset = 0,
                    pageNumber = 0,
                    pageSize = 20,
                    paged = true,
                    unpaged = false,
                    sort =
                        ProductResponse.Sort(
                            empty = false,
                            sorted = true,
                            unsorted = false,
                        ),
                ),
            size = 20,
            sort =
                ProductResponse.Sort(
                    empty = false,
                    sorted = true,
                    unsorted = false,
                ),
            totalElements = 1L,
            totalPages = 1,
        )
}
