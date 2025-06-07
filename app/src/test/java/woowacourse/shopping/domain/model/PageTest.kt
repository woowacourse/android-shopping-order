package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PageTest {
    @Test
    fun `시작 페이지와 종료 페이지가 같으면 한 페이지만 존재함을 알려준다`() {
        // given
        val singlePage = Page(current = 0, isFirst = true, isLast = true)

        // when
        val result = singlePage.isSingle

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isFirst와 isLast가 다르면 단일 페이지가 아님을 알려준다`() {
        // given
        val middlePage = Page(current = 2, isFirst = false, isLast = false)

        // when
        val result = middlePage.isSingle

        // then
        assertThat(result).isFalse()
    }
}
