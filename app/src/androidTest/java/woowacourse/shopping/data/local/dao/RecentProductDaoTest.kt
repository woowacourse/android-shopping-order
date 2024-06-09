package woowacourse.shopping.data.local.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import woowacourse.shopping.data.local.db.AppDatabase
import woowacourse.shopping.data.local.entity.RecentProductEntity
import woowacourse.shopping.recentProduct
import woowacourse.shopping.recentProductEntities
import woowacourse.shopping.recentProductEntity

class RecentProductDaoTest {
    private lateinit var recentProductDao: RecentProductDao

    @Before
    fun setUp() {
        val db =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java,
            ).build()
        recentProductDao = db.recentProductDao()
    }

    @Test
    @DisplayName("최근 본 상품을 저장한다.")
    fun save() =
        runTest {
            val recentWithId = recentProductEntity.copy(productId = 1L)
            val actualId = recentProductDao.save(recentWithId)
            actualId shouldBe 1L
        }


    @Test
    @DisplayName("상품을 지정한 갯수만큼 불러온다")
    fun findAllByLimit() = runTest {
        val expectedSize = recentProductEntities.size
        recentProductEntities.forEach {
            recentProductDao.save(it)
        }
        val expectedEntities = recentProductDao.findAllByLimit(expectedSize)

        expectedEntities.size shouldBe expectedSize
    }


    @Test
    @DisplayName("저장된 상품 하나를 불러온다")
    fun findOrNullSuccess() = runTest {
        val expected = recentProductEntity.copy(productId = 1L)
        recentProductDao.save(expected)

        recentProductDao.findOrNull() shouldBe expected
    }

    @Test
    @DisplayName("저장된 상품이 없으면 null이 리턴된다")
    fun findOrNullFailure() = runTest {
        recentProductDao.findOrNull() shouldBe null
    }
}
