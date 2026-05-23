package woowacourse.shopping.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.localdb.ShoppingDB
import woowacourse.shopping.data.localdb.dao.RecentItemDao
import woowacourse.shopping.data.localdb.entity.RecentItemEntity

class RecentItemDaoTest {
    private lateinit var database: ShoppingDB
    private lateinit var recentItemDao: RecentItemDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room
                .inMemoryDatabaseBuilder(context, ShoppingDB::class.java)
                .build()
        recentItemDao = database.recentItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `최근_본_상품을_저장하면_최신순으로_정렬되어_조회된다`() =
        runTest {
            val recentProduct = createRecentEntity(5)

            recentProduct.forEach { recentItemDao.insert(it) }

            val products = recentItemDao.getRecentItems(10).first()

            assertEquals(5, products.size)
            assertEquals(5, products[0].productId)
            assertEquals(4, products[1].productId)
            assertEquals(3, products[2].productId)
            assertEquals(2, products[3].productId)
            assertEquals(1, products[4].productId)
        }

    @Test
    fun `이미_본_상품을_저장하면_시간이_업데이트되어_최신순으로_정렬된다`() =
        runTest {
            val recentProduct = createRecentEntity(5)

            recentProduct.forEach { recentItemDao.insert(it) }

            val products = recentItemDao.getRecentItems(10).first()

            assertEquals(5, products.size)
            assertEquals(5, products[0].productId) // 가장 최근
            // 첫번째 상품과 Id가 같은 상품
            val product =
                RecentItemEntity(
                    productId = 1,
                    name = "삼품1",
                    imageUrl = "",
                    timestamp = 20,
                )
            recentItemDao.insert(product) // viewAt이 큰 값(최신)
            val newProducts = recentItemDao.getRecentItems(10).first()

            assertEquals(5, newProducts.size)
            assertEquals(1, newProducts[0].productId)
        }

    @Test
    fun `최근_본_상품은_최대_10개까지만_조회된다`() =
        runBlocking {
            val recentProducts = createRecentEntity(15)
            recentProducts.forEach { recentItemDao.insert(it) }

            val products = recentItemDao.getRecentItems(10).first()

            assertEquals(10, products.size)
            assertEquals(15, products[0].productId)
            assertEquals(6, products[9].productId)
        }

    @Test
    fun `최근_본_상품이_없다면_빈목록이_조회된다`() =
        runTest {
            val products = recentItemDao.getRecentItems(10).first()

            assertTrue(products.isEmpty())
        }

    private fun createRecentEntity(count: Long): List<RecentItemEntity> {
        // count가 커질수록 최신
        return (1..count).map {
            RecentItemEntity(
                productId = it,
                name = "상품$it",
                imageUrl = "",
                timestamp = it,
            )
        }
    }
}
