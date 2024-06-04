package woowacourse.shopping.data.cart.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.data.datasource.local.room.ShoppingCartDataBase
import woowacourse.shopping.data.datasource.local.room.dao.CartDao
import woowacourse.shopping.data.datasource.local.room.entity.cart.CartItemEntity
import woowacourse.shopping.domain.model.Quantity
import java.lang.IllegalArgumentException

@RunWith(AndroidJUnit4::class)
class CartDaoTest {
    private lateinit var shoppingCartDataBase: ShoppingCartDataBase
    private lateinit var cartDao: CartDao

    @Before
    fun setUp() {
        shoppingCartDataBase =
            Room.databaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ShoppingCartDataBase::class.java,
                "cart",
            ).build()
        shoppingCartDataBase.clearAllTables()
        cartDao = shoppingCartDataBase.cartDao()
    }

    @After
    fun tearDown() {
        shoppingCartDataBase.clearAllTables()
        shoppingCartDataBase.close()
    }

    @Test
    fun `카트_아이템을_저장한다`() {
        // given
        val cartItemEntity = CartItemEntity(productId = 0L, quantity = Quantity(10))

        // when
        cartDao.insert(cartItemEntity)

        // then
        val actual = cartDao.find(productId = 0L)
        assertThat(actual.productId).isEqualTo(0L)
        assertThat(actual.quantity).isEqualTo(Quantity(10))
    }

    @Test
    fun `카트_아이템의_수량을_변경한다`() {
        // given
        val cartItemEntity = CartItemEntity(productId = 0L, quantity = Quantity(10))
        cartDao.insert(cartItemEntity)

        // when
        cartDao.changeQuantity(productId = 0L, quantity = Quantity(1))

        // then
        val actual = cartDao.find(productId = 0L)
        assertThat(actual.quantity).isEqualTo(Quantity(1))
    }

    @Test
    fun `카트_아이템을_삭제한다`() {
        // given
        val cartItemEntity = CartItemEntity(productId = 0L, quantity = Quantity(10))
        cartDao.insert(cartItemEntity)

        // when
        cartDao.delete(productId = 0L)

        // then
        assertThrows(IllegalArgumentException::class.java) {
            assertThat(cartDao.find(productId = 0L)).isNull()
        }
    }

    @Test
    fun `상품_아이디에_맞는_카트_아이템을_찾는다`() {
        // given
        val cartItemEntity = CartItemEntity(productId = 0L, quantity = Quantity(10))
        val cartItemId = cartDao.insert(cartItemEntity)

        // when
        val actual = cartDao.find(productId = 0L)

        // then
        assertThat(actual.id).isEqualTo(cartItemId)
        assertThat(actual.productId).isEqualTo(0L)
        assertThat(actual.quantity).isEqualTo(Quantity(10))
    }

    @Test
    fun `카트_아이템이_15개_저장되어_있고_첫_페이지를_불러오면_5개가_반환된다`() {
        // given
        val cartItemEntities = List(10) { CartItemEntity(productId = 0L, quantity = Quantity(10)) }
        cartDao.insertAllCartItem(cartItemEntities)

        // when
        val actual = cartDao.findRange(0, 5)

        // then
        assertThat(actual).hasSize(5)
    }

    @Test
    fun `카트_아이템이_3개_저장되어_있고_첫_페이지를_불러오면_3개가_반환된다`() {
        // given
        val cartItemEntities = List(3) { CartItemEntity(productId = 0L, quantity = Quantity(10)) }
        cartDao.insertAllCartItem(cartItemEntities)

        // when
        val actual = cartDao.findRange(0, 5)

        // then
        assertThat(actual).hasSize(3)
    }

    @Test
    fun `카트_아이템의_총_개수를_반환한다`() {
        // given
        val cartItemEntities = List(22) { CartItemEntity(productId = 0L, quantity = Quantity(10)) }
        cartDao.insertAllCartItem(cartItemEntities)

        // when
        val actual = cartDao.totalCount()

        // then
        assertThat(actual).isEqualTo(22)
    }

    private fun CartDao.insertAllCartItem(cartItemEntities: List<CartItemEntity>) {
        cartItemEntities.forEach {
            insert(it)
        }
    }
}
