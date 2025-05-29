package woowacourse.shopping.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RecentProductDaoTest {
    private lateinit var database: ShoppingDatabase
    private lateinit var recentProductDao: RecentProductDao

    @BeforeEach
    fun `테스트_실행_전_인메모리_데이터베이스_생성`() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    ShoppingDatabase::class.java,
                ).allowMainThreadQueries()
                .build()

        recentProductDao = database.recentProductDao()
    }

    private fun insertProducts(vararg products: RecentProductEntity) {
        products.forEach { recentProductDao.insertRecentProduct(it) }
    }

    @Test
    fun `최근_상품을_삽입하고_갯수를_조회할_수_있다`() {
        // Given
        val product = RecentProductEntity(productId = 1L, lastViewedAt = System.currentTimeMillis())

        // When
        recentProductDao.insertRecentProduct(product)
        val count = recentProductDao.getRecentProductCount()

        // Then
        assertThat(count).isEqualTo(1)
    }

    @Test
    fun `최근_상품을_최신순으로_조회하고_개수를_제한할_수_있다`() {
        // Given
        val now = System.currentTimeMillis()
        val products =
            arrayOf(
                RecentProductEntity(productId = 1L, lastViewedAt = now - 3000),
                RecentProductEntity(productId = 2L, lastViewedAt = now - 2000),
                RecentProductEntity(productId = 3L, lastViewedAt = now - 1000),
            )
        insertProducts(*products)

        // When
        val recentProducts = recentProductDao.getRecentProducts(2)

        // Then
        assertThat(recentProducts).hasSize(2)
        assertThat(recentProducts[0].productId).isEqualTo(3L)
        assertThat(recentProducts[1].productId).isEqualTo(2L)
    }

    @Test
    fun `오래된_최근상품을_삭제할_수_있다`() {
        // Given
        val now = System.currentTimeMillis()
        val products =
            arrayOf(
                RecentProductEntity(productId = 1L, lastViewedAt = now - 3000),
                RecentProductEntity(productId = 2L, lastViewedAt = now - 2000),
                RecentProductEntity(productId = 3L, lastViewedAt = now - 1000),
            )
        insertProducts(*products)

        // When
        recentProductDao.deleteOldestRecentProducts(2)
        val remaining = recentProductDao.getRecentProducts(10)

        // Then
        assertThat(remaining).hasSize(1)
        assertThat(remaining[0].productId).isEqualTo(3L)
    }
}
