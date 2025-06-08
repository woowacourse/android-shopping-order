package woowacourse.shopping.fixture

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class FakeOrderRepository(private val size: Int) : OrderRepository {
    private val fakeOrderProducts: List<ProductUiModel> =
        List(size) { index ->
            ProductUiModel(
                id = (index + 1).toLong(),
                name = "${index + 1} 아이스 카페 아메리카노",
                imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                price = 1000 * (index + 1),
            )
        }

    // "주문한 아이템 id" 를 기록용으로 남기고 싶으면 아래처럼 List 로 저장 가능
    val orderedItems = mutableListOf<Long>()

    override suspend fun orderItems(checkedItems: List<Long>): Result<Unit> {
        // 테스트용: 전달받은 checkedItems 를 기록
        orderedItems.clear()
        orderedItems.addAll(checkedItems)

        // 성공 결과 반환
        return Result.success(Unit)
    }
}
