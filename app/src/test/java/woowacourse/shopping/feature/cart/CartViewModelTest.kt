package woowacourse.shopping.feature.cart

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.feature.MainDispatcherExtension
import woowacourse.shopping.feature.fake.FakeCartRepository
import woowacourse.shopping.fixture.TestCartContentFixture
import woowacourse.shopping.fixture.TestProductFixture

@ExtendWith(MainDispatcherExtension::class)
class CartViewModelTest {

    private lateinit var viewModel: CartViewModel

    val comnProducts = TestProductFixture.products(35)
    val comnCartContents = TestCartContentFixture.cartContentsOf(comnProducts)

    @Test
    fun `초기 진입 시 장바구니 항목을 paginate하여 노출한다`() {
        // given: 카트에 35개 상품이 존재한다
        viewModel = CartViewModel(
            initialPageSize = 5,
            cartRepository = FakeCartRepository(
                initial = comnCartContents,
                productCatalog = comnProducts,
            ),
        )

        // when:  initialLoading 을 호출할 때
        viewModel.initialLoading()

        // then:  cartContents state 에 첫 페이지만큼의 항목이 노출된다
        assertThat(viewModel.uiState.value.paginatedCartContents).hasSize(5)
    }

    @Test
    fun `이미 존재하는 상품을 증가시키면 수량이 누적된다`() {
        // given: 카트에 contentId 가 1 인 상품이 2개 담겨 있다
        viewModel = CartViewModel(
            initialPageSize = 5,
            cartRepository = FakeCartRepository(
                initial = listOf(
                    TestCartContentFixture.cartContent(
                        product = comnProducts.first(),
                        quantity = 2,
                        id = "1",
                    ),
                ),
                productCatalog = comnProducts,
            ),
        )
        viewModel.initialLoading()

        // when:  해당 항목의 수량을 증가시킬 때
        viewModel.increase("1")

        // then:  해당 항목의 수량이 3 으로 누적된다
        val expected = viewModel.uiState.value.paginatedCartContents.first().productUiModel.quantity
        assertThat(expected).isEqualTo(3)
    }

    @Test
    fun `보유 수량보다 적게 감소시키면 수량이 줄어든다`() {
        // given: 카트에 하나의 상품이 3개 담겨 있다
        viewModel = CartViewModel(
            initialPageSize = 5,
            cartRepository = FakeCartRepository(
                initial = listOf(
                    TestCartContentFixture.cartContent(
                        product = comnProducts.first(),
                        quantity = 3,
                    ),
                ),
                productCatalog = comnProducts,
            ),
        )
        viewModel.initialLoading()
        val contentId = viewModel.uiState.value.paginatedCartContents.first().contentId

        // when:  해당 항목을 1개 감소시킬 때
        viewModel.decrease(contentId)

        // then:  해당 항목의 수량이 2 가 된다
        val expected = viewModel.uiState.value.paginatedCartContents.first().productUiModel.quantity
        assertThat(expected).isEqualTo(2)
    }

    @Test
    fun `보유 수량과 같은 수량을 감소시키면 해당 상품이 제거된다`() {
        // given: 카트에 하나의 상품이 1개 담겨 있다
        viewModel = CartViewModel(
            initialPageSize = 5,
            cartRepository = FakeCartRepository(
                initial = listOf(
                    TestCartContentFixture.cartContent(
                        product = comnProducts.first(),
                        quantity = 1,
                    ),
                ),
                productCatalog = comnProducts,
            ),
        )
        viewModel.initialLoading()
        val contentId = viewModel.uiState.value.paginatedCartContents.first().contentId

        // when:  해당 항목을 1개 감소시킬 때
        viewModel.decrease(contentId)

        // then:  카트에서 해당 항목이 제거된다
        assertThat(viewModel.uiState.value.paginatedCartContents).isEmpty()
    }

    @Test
    fun `더이상 불러올 상품이 없으면 isEndPage가 true가 된다`() {
        // given: 7개 항목이 담긴 뷰모델이 주어진다
        viewModel = CartViewModel(
            initialPageSize = 5,
            cartRepository = FakeCartRepository(
                initial = TestCartContentFixture.cartContentsOf(comnProducts.take(7)),
                productCatalog = comnProducts,
            ),
        )
        viewModel.initialLoading()
        assertThat(viewModel.isEndPage()).isFalse()

        // when:  다음 페이지로 이동할 때
        viewModel.moveToNextPage()

        // then:  isEndPage 가 true 가 된다
        assertThat(viewModel.isEndPage()).isTrue()
    }

    @Test
    fun `각 항목의 체크박스를 토글하면 cartItemIds에 반영된다`() {
        // given: 카트 항목이 노출되어 있고 모든 체크 상태가 false 이다
        viewModel = CartViewModel(
            initialPageSize = 5,
            cartRepository = FakeCartRepository(
                initial = TestCartContentFixture.cartContentsOf(comnProducts.take(3)),
                productCatalog = comnProducts,
            ),
        )
        viewModel.initialLoading()
        val contentId = viewModel.uiState.value.paginatedCartContents.first().contentId
        assertThat(viewModel.uiState.value.checkMap[contentId]).isFalse()

        // when:  특정 항목의 체크박스를 토글할 때
        viewModel.cartItemCheck(contentId)

        // then:  checkMap 에 해당 contentId 가 true 로 반영된다
        assertThat(viewModel.uiState.value.checkMap[contentId]).isTrue()
    }

    @Test
    fun `전체 체크박스를 누르면 현재 페이지의 모든 항목이 선택된다`() {
        // given: 현재 페이지에 3 개의 항목이 노출되어 있다
        viewModel = CartViewModel(
            initialPageSize = 5,
            cartRepository = FakeCartRepository(
                initial = TestCartContentFixture.cartContentsOf(comnProducts.take(3)),
                productCatalog = comnProducts,
            ),
        )
        viewModel.initialLoading()

        // when:  전체 체크박스를 누를 때
        viewModel.totalCheck()

        // then:  checkMap 의 모든 항목이 true 로 선택된다
        assertThat(viewModel.uiState.value.checkMap).hasSize(3)
        assertThat(viewModel.uiState.value.checkMap.values).allMatch { it }
    }

    @Test
    fun `전체 체크박스를 다시 누르면 현재 페이지의 모든 항목이 선택 해제된다`() {
        // given: 전체 체크박스를 눌러 현재 페이지의 모든 항목이 선택된 상태이다
        viewModel = CartViewModel(
            initialPageSize = 5,
            cartRepository = FakeCartRepository(
                initial = TestCartContentFixture.cartContentsOf(comnProducts.take(3)),
                productCatalog = comnProducts,
            ),
        )
        viewModel.initialLoading()
        viewModel.totalCheck()
        assertThat(viewModel.uiState.value.checkMap.values).allMatch { it }

        // when:  전체 체크박스를 다시 누를 때
        viewModel.totalCheck()

        // then:  checkMap 의 모든 항목이 false 로 선택 해제된다
        assertThat(viewModel.uiState.value.checkMap.values).noneMatch { it }
    }

    @Test
    fun `선택된 항목의 합산 금액과 수량이 노출된다`() {
        // given: 가격이 1000 원인 상품 1개, 2000 원인 상품 2개 가 담겨 있고 두 번째 항목만 선택된다
        viewModel = CartViewModel(
            initialPageSize = 5,
            cartRepository = FakeCartRepository(
                initial = listOf(
                    TestCartContentFixture.cartContent(product = comnProducts[0], quantity = 1),
                    TestCartContentFixture.cartContent(product = comnProducts[1], quantity = 2),
                ),
                productCatalog = comnProducts,
            ),
        )
        viewModel.initialLoading()
        val secondContentId = viewModel.uiState.value.paginatedCartContents[1].contentId

        // when:  두 번째 항목의 체크박스를 토글할 때
        viewModel.cartItemCheck(secondContentId)

        // then:  totalPrice 가 선택된 항목 기준으로 4000 으로 노출된다
        assertThat(viewModel.uiState.value.totalPrice).isEqualTo(4000)
    }
}
