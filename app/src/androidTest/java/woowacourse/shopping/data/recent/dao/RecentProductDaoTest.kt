package woowacourse.shopping.data.recent.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.data.local.ShoppingCartDataBase
import woowacourse.shopping.data.recent.local.dao.RecentProductDao
import woowacourse.shopping.data.recent.local.entity.ProductEntity
import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class RecentProductDaoTest {
    private lateinit var shoppingCartDataBase: ShoppingCartDataBase
    private lateinit var recentProductDao: RecentProductDao

    @Before
    fun setUp() {
        shoppingCartDataBase =
            Room.databaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ShoppingCartDataBase::class.java,
                "shopping_cart",
            ).build()
        shoppingCartDataBase.clearAllTables()
        recentProductDao = shoppingCartDataBase.recentProductDao()
    }

    @After
    fun tearDown() {
        shoppingCartDataBase.clearAllTables()
        shoppingCartDataBase.close()
    }

    @Test
    fun `상품_id에_맞는_최근_상품을_불러온다`() {
        // given
        val recentProductEntity = RecentProductEntity(product = productEntity, seenDateTime = seenDateTime)
        recentProductDao.insert(recentProductEntity)

        // when
        val actual = recentProductDao.findOrNullByProductId(productId)

        // then
        assertThat(actual).isNotNull
        assertThat(actual?.product?.productId).isEqualTo(productId)
        assertThat(actual?.seenDateTime).isEqualTo(seenDateTime)
    }

    @Test
    fun `최근_본_상품_5개를_가져온다`() {
        // given
        val recentProductEntities =
            List(10) {
                RecentProductEntity(product = ProductEntity(it), seenDateTime = LocalDateTime.of(2024, 5, 25, 3, it))
            }
        recentProductDao.insertAll(recentProductEntities)

        // when
        val actual = recentProductDao.findRange(5)

        // then
        val expectedId = listOf(9, 8, 7, 6, 5)
        assertThat(actual).hasSize(5)
        assertThat(actual.map { it.product.productId }).isEqualTo(expectedId)
    }

    @Test
    fun `최근_본_상품을_저장한다`() {
        // given
        val recentProductEntity = RecentProductEntity(product = productEntity, seenDateTime = seenDateTime)

        // when
        recentProductDao.insert(recentProductEntity)

        // then
        val actual = recentProductDao.findOrNullByProductId(productId)
        assertThat(actual).isNotNull
        assertThat(actual?.product?.productId).isEqualTo(productId)
        assertThat(actual?.seenDateTime).isEqualTo(seenDateTime)
    }

    @Test
    fun `상품_id에_맞는_최근_본_상품의_시간을_변경한다`() {
        // given
        val recentProductEntity = RecentProductEntity(product = productEntity, seenDateTime = seenDateTime)
        recentProductDao.insert(recentProductEntity)

        // when
        val expectedSeenDateTime = LocalDateTime.of(2024, 5, 25, 4, 0)
        recentProductDao.update(productId, expectedSeenDateTime)

        // then
        val actual = recentProductDao.findOrNullByProductId(productId)
        assertThat(actual).isNotNull
        assertThat(actual?.seenDateTime).isEqualTo(expectedSeenDateTime)
    }

    private fun RecentProductDao.insertAll(recentProductEntities: List<RecentProductEntity>) {
        recentProductEntities.forEach { insert(it) }
    }

    private fun ProductEntity(productId: Int): ProductEntity {
        return productEntity.copy(productId = productId)
    }

    companion object {
        private val productId = 0
        private val productEntity = ProductEntity(productId, "올리브", 1500, "https://github.com/", "food")
        private val seenDateTime = LocalDateTime.of(2024, 5, 25, 3, 30)
    }
}
