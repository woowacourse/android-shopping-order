package woowacourse.shopping.ui.productlist

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ProductPageStateHolderTest {
    @Test
    fun `페이지를 순차 로드하면 상품 id가 누적된다`() {
        val holder = ProductPageStateHolder(initialPage = 0)

        holder.peekNextPage() shouldBe 0
        holder.onPageLoaded(
            productIds = listOf(1L, 2L, 3L),
            hasNextPage = true,
            replaceExisting = true,
        )

        holder.peekNextPage() shouldBe 1
        holder.onPageLoaded(
            productIds = listOf(4L, 5L),
            hasNextPage = false,
            replaceExisting = false,
        )

        holder.displayedProductIds() shouldBe listOf(1L, 2L, 3L, 4L, 5L)
        holder.canLoadNextPage() shouldBe false
        holder.peekNextPage() shouldBe 1
    }

    @Test
    fun `replaceExisting이 true이면 누적 목록을 초기화하고 다시 구성한다`() {
        val holder = ProductPageStateHolder(initialPage = 0)

        holder.onPageLoaded(
            productIds = listOf(1L),
            hasNextPage = true,
            replaceExisting = true,
        )
        holder.onPageLoaded(
            productIds = listOf(2L),
            hasNextPage = false,
            replaceExisting = false,
        )

        holder.onPageLoaded(
            productIds = listOf(10L, 11L),
            hasNextPage = true,
            replaceExisting = true,
        )

        holder.displayedProductIds() shouldBe listOf(10L, 11L)
        holder.canLoadNextPage() shouldBe true
        holder.peekNextPage() shouldBe 2
    }

    @Test
    fun `reset을 호출하면 페이지 상태가 초기화된다`() {
        val holder = ProductPageStateHolder(initialPage = 0)

        holder.onPageLoaded(
            productIds = listOf(11L, 12L),
            hasNextPage = false,
            replaceExisting = true,
        )

        holder.reset(startPage = 2)

        holder.peekNextPage() shouldBe 2
        holder.displayedProductIds() shouldBe emptyList()
        holder.canLoadNextPage() shouldBe true
    }
}
