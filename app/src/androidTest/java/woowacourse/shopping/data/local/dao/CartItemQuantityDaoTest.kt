package woowacourse.shopping.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.data.localdb.ShoppingDB
import woowacourse.shopping.data.localdb.dao.CartItemQuantityDao
import woowacourse.shopping.data.localdb.entity.CartItemQuantityEntity
import kotlin.jvm.java

@RunWith(AndroidJUnit4::class)
class CartItemQuantityDaoTest {
    private lateinit var database: ShoppingDB
    private lateinit var cartItemQuantityDao: CartItemQuantityDao

    private val cartItemQuantityEntity =
        CartItemQuantityEntity(
            productId = 1,
            cartItemId = 1,
            quantity = 5,
        )

    private val cartItemQuantityEntity1 =
        CartItemQuantityEntity(
            productId = 1,
            cartItemId = 1,
            quantity = 2,
        )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room
                .inMemoryDatabaseBuilder(context, ShoppingDB::class.java)
                .build()
        cartItemQuantityDao = database.cartItemQuantityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `장바구니_저장_테스트`() =
        runTest {
            cartItemQuantityDao.insert(cartItemQuantityEntity)

            val items = cartItemQuantityDao.getAll().first()
            assertEquals(1, items.size)
            assertEquals(1, items[0].productId)
            assertEquals(1, items[0].cartItemId)
            assertEquals(5, items[0].quantity)
        }

    @Test
    fun `장바구니_수량_업데이트_테스트`() =
        runTest {
            cartItemQuantityDao.insert(cartItemQuantityEntity)

            cartItemQuantityDao.insert(cartItemQuantityEntity1)

            val items = cartItemQuantityDao.getAll().first()

            assertEquals(1, items.size)
            assertEquals(2, items[0].quantity)
        }

    @Test
    fun `장바구니_삭제_테스트`() =
        runTest {
            cartItemQuantityDao.insert(cartItemQuantityEntity)

            val items = cartItemQuantityDao.getAll().first()
            assertEquals(1, items.size)

            cartItemQuantityDao.deleteByProductId(1)

            val deletedItems = cartItemQuantityDao.getAll().first()

            assertTrue(deletedItems.isEmpty())
        }
}
