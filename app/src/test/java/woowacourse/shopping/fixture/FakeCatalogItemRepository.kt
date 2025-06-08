package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class FakeCatalogItemRepository(
    private val size: Int,
) : ProductsRepository {
    private val fakeProducts: List<ProductUiModel> =
        List(size) { index ->
            ProductUiModel(
                id = (index + 1).toLong(),
                name = "${index + 1} 아이스 카페 아메리카노",
                imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                price = 1000 * (index + 1),
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
                products = pageItems,
                page = page,
                hasNext = endIndex < fakeProducts.size,
                hasPrevious = page > 0,
            )

        return Result.success(pagingData)
    }

    override suspend fun getProductById(id: Long): Result<ProductUiModel> {
        val product = fakeProducts.find { it.id == id }
        return if (product != null) {
            Result.success(product)
        } else {
            Result.failure(Exception("product를 찾을 수 없습니다"))
        }
    }

    override suspend fun getRecommendedProductsFromLastViewed(cartProductIds: List<Long>): Result<List<ProductUiModel>> {
        val recommended = fakeProducts.filterNot { it.id in cartProductIds }.take(5)
        return Result.success(recommended)
    }
}
