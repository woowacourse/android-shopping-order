package woowacourse.shopping.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.DUMMY_HISTORY_PRODUCT_1
import woowacourse.shopping.DUMMY_HISTORY_PRODUCT_2
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.util.CoroutinesTestExtension

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@Suppress("ktlint:standard:function-naming")
class HistoryDaoTest {
    private lateinit var database: ShoppingDatabase
    private lateinit var dao: HistoryDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room
                .inMemoryDatabaseBuilder(context, ShoppingDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.historyDao()
    }

    @Test
    fun 검색_기록을_저장하고_조회한다() =
        runTest {
            // given
            val historyProduct = DUMMY_HISTORY_PRODUCT_1
            dao.insertHistory(historyProduct)

            // when
            val result = dao.getHistoryProducts()

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].productId).isEqualTo(DUMMY_HISTORY_PRODUCT_1.productId)
        }

    @Test
    fun 검색_기록을_최신순으로_조회한다() =
        runTest {
            // given
            dao.insertHistory(DUMMY_HISTORY_PRODUCT_1.copy(timestamp = 1))
            dao.insertHistory(DUMMY_HISTORY_PRODUCT_2.copy(timestamp = 0))

            // when
            val result = dao.getHistoryProducts()

            // then
            assertThat(
                result.map { it.productId },
            ).containsExactly(DUMMY_HISTORY_PRODUCT_1.productId, DUMMY_HISTORY_PRODUCT_2.productId).inOrder()
        }

    @Test
    fun 검색_기록이_제한을_초과하면_오래된_기록부터_삭제된다() =
        runTest {
            // given
            repeat(5) { index ->
                dao.insertHistory(
                    DUMMY_HISTORY_PRODUCT_1.copy(
                        productId = index.toLong(),
                        timestamp = index.toLong(),
                    ),
                )
            }

            // when
            dao.insertHistoryWithLimit(
                history = DUMMY_HISTORY_PRODUCT_2,
                limit = 5,
            )

            // then
            val result = dao.getHistoryProducts()
            assertThat(result).hasSize(5)
            assertThat(result.any { it.productId == 0L }).isFalse()
            assertThat(result.first().productId).isEqualTo(DUMMY_HISTORY_PRODUCT_2.productId)
        }

    @Test
    fun 최근_검색_기록을_조회한다() =
        runTest {
            // given
            dao.insertHistory(DUMMY_HISTORY_PRODUCT_1.copy(timestamp = 0))
            dao.insertHistory(DUMMY_HISTORY_PRODUCT_2.copy(timestamp = 1))

            // when
            val result = dao.getRecentHistoryProduct()

            // then
            assertThat(result?.productId).isEqualTo(DUMMY_HISTORY_PRODUCT_2.productId)
        }

    @Test
    fun 총_검색_기록_개수를_조회한다() =
        runTest {
            // given
            dao.insertHistory(DUMMY_HISTORY_PRODUCT_1)
            dao.insertHistory(DUMMY_HISTORY_PRODUCT_2)

            // when
            val count = dao.getHistoryCount()

            // then
            assertThat(count).isEqualTo(2)
        }

    @Test
    fun 오래된_검색_기록을_원하는_개수만큼_삭제한다() =
        runTest {
            // given
            val base = System.currentTimeMillis()
            repeat(5) { index ->
                dao.insertHistory(
                    DUMMY_HISTORY_PRODUCT_1.copy(
                        productId = index.toLong(),
                        timestamp = base + index,
                    ),
                )
            }

            // when
            dao.deleteOldestHistories(2)

            // then
            val result = dao.getHistoryProducts()
            assertThat(result).hasSize(3)
            assertThat(result.map { it.productId }).doesNotContain(0)
            assertThat(result.map { it.productId }).doesNotContain(1)
        }

    @After
    fun tearDown() {
        database.close()
    }
}
