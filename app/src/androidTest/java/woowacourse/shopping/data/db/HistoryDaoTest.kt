package woowacourse.shopping.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.data.db.dao.HistoryDao
import woowacourse.shopping.data.db.entity.HistoryEntity
import woowacourse.shopping.fixture.fakeContext
import woowacourse.shopping.fixture.historyEntityFixture1
import woowacourse.shopping.fixture.historyEntityListFixture

@RunWith(AndroidJUnit4::class)
class HistoryDaoTest {
    private lateinit var db: PetoMarketDatabase
    private lateinit var historyDao: HistoryDao

    @Before
    fun setUp() {
        db =
            Room.databaseBuilder(
                fakeContext,
                PetoMarketDatabase::class.java,
                "peto_market_database",
            )
                .allowMainThreadQueries()
                .build()

        historyDao = db.historyDao()
    }

    @Test
    fun `새로운_기록을_추가한다`() =
        runTest {
            // when
            val entity = historyEntityFixture1

            // given
            historyDao.insert(entity)

            // then
            val actual = historyDao.getLatestHistories()
            assertEquals(actual, listOf(entity))
        }

    @Test
    fun `최근_10개의_기록을_최신순으로_가져온다`() =
        runTest {
            // given
            historyEntityListFixture.forEach {
                historyDao.insert(it)
            }
            val expected =
                (2L..11L).map { id ->
                    HistoryEntity(productId = id, createdAt = id)
                }.reversed()

            // when
            val actual = historyDao.getLatestHistories()

            // then
            assertEquals(expected, actual)
        }

    @After
    fun tearDown() {
        db.close()
    }
}
