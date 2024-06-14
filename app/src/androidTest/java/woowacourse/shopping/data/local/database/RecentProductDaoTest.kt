package woowacourse.shopping.data.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertAll
import org.junit.runner.RunWith
import woowacourse.shopping.util.getMultipleRecentProductFixtureItems
import woowacourse.shopping.util.getSingleRecentProductFixtureItem
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class RecentProductDaoTest {
    private lateinit var database: RecentProductDatabase
    private lateinit var dao: RecentProductDao

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RecentProductDatabase::class.java,
            ).allowMainThreadQueries().build()
        dao = database.recentProductDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `최근_본_상품을_추가할_수_있다`() =
        runTest {
            // when
            dao.save(
                getSingleRecentProductFixtureItem(
                    productId = 1,
                    productName = "사과1",
                    imageUrl = "image1",
                    dateTime = "2024-01-01 12:00",
                    category = "test",
                ),
            )
            val actualResult = dao.findByProductId(1)

            // then
            assertThat(actualResult).isEqualTo(
                RecentProductEntity(
                    id = 1,
                    productId = 1,
                    productName = "사과1",
                    imageUrl = "image1",
                    dateTime = "2024-01-01 12:00",
                    category = "test",
                ),
            )
        }

    @Test
    fun `여러_개의_상품을_저장했을_때_가장_마지막에_저장한_상품이_최근_본_상품이다`() =
        runTest {
            // given
            getMultipleRecentProductFixtureItems(
                count = 5,
                productNamePrefix = "사과",
                imageUrlPrefix = "image",
                category = "fruit",
            ).forEach { dao.save(it) }

            // when
            val actualResult = dao.findMostRecentProduct()

            // then
            assertAll(
                { assertThat(actualResult).isNotNull },
                { assertThat(actualResult?.id).isEqualTo(5) },
                { assertThat(actualResult?.productId).isEqualTo(5) },
                { assertThat(actualResult?.productName).isEqualTo("사과5") },
                { assertThat(actualResult?.imageUrl).isEqualTo("image5") },
                { assertThat(actualResult?.category).isEqualTo("fruit") },
            )
        }

    @Test
    fun `정해진_수량_만큼의_열람_이력을_가져올_수_있다`() =
        runTest {
            // given
            getMultipleRecentProductFixtureItems(
                count = 5,
                productNamePrefix = "사과",
                imageUrlPrefix = "image",
                category = "fruit",
            ).forEach { dao.save(it) }

            // when
            val actualResult = dao.findAll(5)

            // then
            assertThat(actualResult).hasSize(5)
        }

    @Test
    fun `열람_이력_데이터를_변경할_수_있다`() =
        runTest {
            // given
            getMultipleRecentProductFixtureItems(
                count = 5,
                productNamePrefix = "사과",
                imageUrlPrefix = "image",
                category = "fruit",
            ).forEach { dao.save(it) }
            val localDateTime = LocalDateTime.now().toString()

            // when
            dao.update(1, localDateTime)
            val actualResult = dao.findByProductId(1)

            // then
            assertThat(actualResult?.dateTime).isEqualTo(localDateTime)
        }

    @Test
    fun `모든_열람_이력을_삭제할_수_있다`() =
        runTest {
            // given
            getMultipleRecentProductFixtureItems(
                count = 5,
                productNamePrefix = "사과",
                imageUrlPrefix = "image",
                category = "fruit",
            ).forEach { dao.save(it) }

            // when
            dao.deleteAll()
            val actualResult = dao.findAll(5)

            // then
            assertThat(actualResult).hasSize(0)
        }
}
