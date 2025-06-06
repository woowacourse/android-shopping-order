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

    override fun getProducts(
        page: Int,
        size: Int,
        onResult: (Result<PagingData>) -> Unit,
    ) {
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

        onResult(Result.success(pagingData))
    }

    override fun getProductById(
        id: Long,
        onResult: (Result<ProductUiModel>) -> Unit,
    ) {
        val product = fakeProducts.find { it.id == id }
        if (product != null) {
            onResult(Result.success(product))
        } else {
            onResult(Result.failure(NoSuchElementException("product를 찾을 수 없습니다")))
        }
    }

    override fun getRecommendedProductsFromLastViewed(
        cartProductIds: List<Long>,
        onResult: (Result<List<ProductUiModel>>) -> Unit,
    ) {
        val recommended = fakeProducts.filterNot { it.id in cartProductIds }.take(5)
        onResult(Result.success(recommended))
    }
}
