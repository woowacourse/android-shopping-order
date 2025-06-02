package woowacourse.shopping.presentation.view

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.fixture.FakeCartRepository
import woowacourse.shopping.fixture.FakeProductRepository
import woowacourse.shopping.presentation.view.order.suggestion.SuggestionViewModel
import woowacourse.shopping.presentation.view.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.view.util.getOrAwaitValue

@ExtendWith(InstantTaskExecutorExtension::class)
class SuggestionViewModelTest {
    private lateinit var viewModel: SuggestionViewModel

    @BeforeEach
    fun setup() {
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository()
        viewModel = SuggestionViewModel(fakeCartRepository, fakeProductRepository)
    }

    @Test
    fun `초기화 시 추천 상품이 로드된다`() {
        // When
        val items = viewModel.suggestionProducts.getOrAwaitValue()

        // Then
        assertAll(
            { assertThat(items).isNotNull },
            { assertThat(items).isNotEmpty },
        )
    }
}
