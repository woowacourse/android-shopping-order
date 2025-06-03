package woowacourse.shopping.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.fake.history.FakeHistoryDao
import woowacourse.shopping.data.fake.history.FakeHistoryDataSource
import woowacourse.shopping.fixture.historyEntityFixtures

class DefaultHistoryRepositoryTest {
    private lateinit var repository: DefaultHistoryRepository

    @BeforeEach
    fun setUp() {
        val dao = FakeHistoryDao(historyEntityFixtures.toMutableList())
        val dataSource = FakeHistoryDataSource(dao)
        repository = DefaultHistoryRepository(dataSource)
    }

    @Test
    fun `최근 본 상품을 추가한다`() =
        runTest {
            repository.saveHistory(11)

            val expected = repository.getHistories()

            assertTrue(expected.contains(11))
        }

    @Test
    fun `최근 본 상품을 시간순으로 내림차순 정렬하여 가져온다`() =
        runTest {
            val result = repository.getHistories()

            val expected =
                historyEntityFixtures
                    .sortedByDescending { it.createdAt }
                    .map { it.productId }

            assertEquals(expected, result)
        }
}
