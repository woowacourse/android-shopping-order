package woowacourse.shopping

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.data.local.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.local.database.DataBase
import woowacourse.shopping.data.local.entity.RecentlyViewedProductEntity

@RunWith(AndroidJUnit4::class)
class RecentlyViewedProductDaoTest {
    private lateinit var db: DataBase
    private lateinit var dao: RecentlyViewedProductDao

    @Test
    fun `최근_본_상품을_불러올_때_최신순으로_정렬된다`() =
        runBlocking {
            // given
            createDb()
            val entity1 = RecentlyViewedProductEntity(id = 1L, timeStamp = 1L)
            val entity2 = RecentlyViewedProductEntity(id = 2L, timeStamp = 2L)

            // when
            dao.insert(entity1)
            dao.insert(entity2)

            // then
            val history = dao.getAll().first()
            assertEquals(2, history?.size)
            assertEquals(2L, history?.get(0)?.id)
            assertEquals(1L, history?.get(1)?.id)
            closeDb()
        }

    @Test
    fun `최근_본_상품의_목록은_최대_10개까지_저장된다`() =
        runBlocking {
            // given
            createDb()
            for (i in 0..15) {
                dao.enqueueAndLimit10(
                    RecentlyViewedProductEntity(
                        id = i.toLong(),
                        timeStamp = i.toLong(),
                    ),
                )
            }

            // when
            val history = dao.getAll().first()

            // then
            assertEquals(10, history?.size)
        }

    private fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DataBase::class.java).build()
        dao = db.recentlyViewedProductDao()
    }

    private fun closeDb() {
        db.close()
    }
}
