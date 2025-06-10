package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.mapper.toUiModel

class FakeCatalogItemRepository(
    private val size: Int,
) : ProductsRepository {
    private val fakeProducts: List<Product> =
        List(size) { index ->
            Product(
                id = (index + 1).toLong(),
                name = "${index + 1} 아이스 카페 아메리카노",
                imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                price = 1000 * (index + 1),
                quantity = (index + 1),
                category = "",
            )
        }

    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): Result<PagingData> {
        val startIndex = page * size
        val endIndex = (startIndex + size).coerceAtMost(fakeProducts.size)

        val pageItems =
            if (startIndex < fakeProducts.size) {
                fakeProducts.subList(startIndex, endIndex)
            } else {
                emptyList()
            }

        val pagingData =
            PagingData(
                products = pageItems.map { it.toUiModel() },
                page = page,
                hasNext = endIndex < fakeProducts.size,
                hasPrevious = page > 0,
            )

        return Result.success(pagingData)
    }

    override suspend fun getProductById(id: Long): Result<Product> {
        val product = fakeProducts.find { it.id == id }
        return if (product != null) {
            Result.success(product)
        } else {
            Result.failure(Exception("product를 찾을 수 없습니다"))
        }
    }

    override suspend fun getRecommendedProductsFromLastViewed(cartProductIds: List<Long>): Result<List<Product>> {
        val recommended: List<Product> = fakeProducts.filterNot { it.id in cartProductIds }.take(5)
        return Result.success(recommended)
    }
}
