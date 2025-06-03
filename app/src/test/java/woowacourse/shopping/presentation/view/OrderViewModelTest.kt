package woowacourse.shopping.presentation.view

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.fixture.FakeCartRepository
import woowacourse.shopping.fixture.productsFixture
import woowacourse.shopping.presentation.model.FetchPageDirection
import woowacourse.shopping.presentation.view.order.OrderViewModel
import woowacourse.shopping.presentation.view.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.view.util.getOrAwaitValue

@ExtendWith(InstantTaskExecutorExtension::class)
class OrderViewModelTest {
    private lateinit var viewModel: OrderViewModel
    private val dummyCartProducts =
        productsFixture.take(10).map {
            CartProduct(it.id, it.toDomain(), 2)
        }

    @BeforeEach
    fun setUp() {
        val fakeCartRepository = FakeCartRepository(dummyCartProducts)
        viewModel =
            OrderViewModel(fakeCartRepository)
    }

    @Test
    fun `초기화 시 장바구니 아이템이 로드된다`() {
        // When
        val items = viewModel.cartProducts.getOrAwaitValue()

        // Then
        assertAll(
            { assertThat(items).isNotNull },
            { assertThat(items).isNotEmpty },
        )
    }

    @Test
    fun `다음 페이지 요청 시 페이지 증가 및 아이템이 추가된다`() {
        // Given
        val before = viewModel.cartProducts.getOrAwaitValue()

        // When
        viewModel.fetchCartItems(FetchPageDirection.NEXT)
        val after = viewModel.cartProducts.getOrAwaitValue()

        // Then
        assertAll(
            { assertThat(after.size).isGreaterThanOrEqualTo(before.size) },
            { assertThat(viewModel.page.getOrAwaitValue()).isEqualTo(2) },
        )
    }

    @Test
    fun `삭제 성공 시 현재 페이지를 다시 조회한다`() {
        // Given
        val items = viewModel.cartProducts.getOrAwaitValue()
        val target = items.last()

        // When
        viewModel.onDeleteProduct(target.productId)
        val newItems = viewModel.cartProducts.getOrAwaitValue()

        // Then
        assertAll(
            { assertThat(newItems).doesNotContain(target) },
            { assertThat(newItems.size).isEqualTo(items.size) },
            { assertThat(newItems.last().productId).isNotEqualTo(target.productId) },
        )
    }

    @Test
    fun `다음 페이지에 조회 가능한 상품 여부이 있다면 다음 페이지 조회 가능 상태가 True로 설정된다`() {
        // When
        val hasMore = viewModel.hasMore.getOrAwaitValue()

        // Then
        assertThat(hasMore).isTrue()
    }

    @Test
    fun `특정_상품의_구매_수량을_증가시킬_수_있다`() {
        // When
        val before = viewModel.cartProducts.getOrAwaitValue()
        val target = before.first()
        viewModel.increaseQuantity(target.productId)

        // Then
        val after = viewModel.cartProducts.getOrAwaitValue()
        assertThat(after.first().quantity).isGreaterThan(target.quantity)
    }

    @Test
    fun `특정_상품의_구매_수량을_감소시킬_수_있다`() {
        // Give
        val before = viewModel.cartProducts.getOrAwaitValue()
        val target = before.first()

        // When
        viewModel.decreaseQuantity(target.productId)

        // Then
        val after = viewModel.cartProducts.getOrAwaitValue()
        assertThat(after.first().quantity).isLessThan(target.quantity)
    }

    @Test
    fun `구매_수량이_1인_특정_상품의_구매_수량을_감소시키면_삭제된다`() {
        // When
        val before = viewModel.cartProducts.getOrAwaitValue()
        val target = before.first()
        viewModel.decreaseQuantity(target.productId)
        viewModel.decreaseQuantity(target.productId)

        // Then
        val after = viewModel.cartProducts.getOrAwaitValue()
        assertThat(after).doesNotContain(target)
    }

    @Test
    fun `특정 상품을 주문 선택에 추가하면 orderProducts에 포함된다`() {
        // Given
        val target = viewModel.cartProducts.getOrAwaitValue().first()

        // When
        viewModel.onSelectOrderProduct(target.productId)

        // Then
        val orderProducts = viewModel.totalOrderCount.getOrAwaitValue()
        assertThat(orderProducts).isEqualTo(target.quantity)
    }

    @Test
    fun `전체 선택하면 orderProducts에 모든 상품이 포함된다`() {
        // When
        viewModel.toggleSelectAll()

        // Then
        val orderProducts = viewModel.totalOrderCount.getOrAwaitValue()
        val expected = dummyCartProducts.sumOf { it.quantity }

        assertThat(orderProducts).isEqualTo(expected)
    }

    @Test
    fun `선택된 주문 상품들의 총 주문 가격과 총 수량이 올바르게 계산된다`() {
        // Given
        val target = viewModel.cartProducts.getOrAwaitValue().first()
        viewModel.onSelectOrderProduct(target.productId)

        // When
        val totalCount = viewModel.totalOrderCount.getOrAwaitValue()
        val totalPrice = viewModel.totalOrderPrice.getOrAwaitValue()

        // Then
        assertThat(totalCount).isEqualTo(target.quantity)
        assertThat(totalPrice).isEqualTo(target.totalPrice)
    }
}
