package woowacourse.shopping.ui.recentproduct

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.database.dao.recentproduct.RecentProductDao
import woowacourse.shopping.data.database.dao.recentproduct.RecentProductDaoImpl
import woowacourse.shopping.data.datasource.response.RecentProductEntity
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.ui.ProductFixture

@RunWith(RobolectricTestRunner::class)
class RecentProductDaoTest {

    private lateinit var recentProductDao: RecentProductDao
    private lateinit var database: SQLiteOpenHelper

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = ShoppingDatabase(context)
        recentProductDao = RecentProductDaoImpl(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `최근 본 상품의 데이터 베이스에 최근 본 상품을 추가할 수 있다`() {
        // given
        val product = ProductFixture.createProduct().toEntity()

        // when
        recentProductDao.add(product)

        // then
        val actual = recentProductDao.getPartially(1)
        val expected = listOf(
            RecentProductEntity(
                id = 1,
                product = product
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `현재 데이터베이스에 존재하는 최근 본 상품의 데이터 개수를 반환한다`() {
        // given
        val recentProducts = ProductFixture.createProducts().map {
            it.toEntity()
        }

        recentProducts.forEach {
            recentProductDao.add(it)
        }

        // when
        val actual: Int = recentProductDao.getSize()

        // then
        val expected = recentProducts.size

        assertEquals(expected, actual)
    }

    @Test
    fun `더 오래전에 저장된 최근 본 상품의 데이터부터 원하는 개수만큼 반환한다`() {
        // given
        val recentProducts = ProductFixture.createProducts().map {
            it.toEntity()
        }
        val size = 2

        recentProducts.forEach {
            recentProductDao.add(it)
        }

        // when
        val actual: List<RecentProductEntity> = recentProductDao.getPartially(size)

        // then
        val expected = recentProducts.subList(0, size)
            .mapIndexed { id, product ->
                RecentProductEntity(
                    id = id + INITIAL_ID,
                    product = product
                )
            }.reversed()

        assertEquals(expected, actual)
    }

    @Test
    fun `이미 최근 본 상품의 데이터 베이스에 존재하는 상품 데이터가 다시 추가될 때 기존의 데이터는 삭제하고 데이터를 삽입한다`() {
        // given
        val recentProducts = ProductFixture.createProducts().map {
            it.toEntity()
        }
        val product = recentProducts.first()

        recentProducts.forEach {
            recentProductDao.add(it)
        }

        // when
        recentProductDao.add(product)

        val actual = recentProductDao.getPartially(recentProducts.size).count {
            it.product == product
        }

        // then
        val expected = 1

        assertEquals(expected, actual)
    }

    companion object {
        private const val INITIAL_ID = 1
    }
}
