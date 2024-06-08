package com.example.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecentProductEntityDaoTestEntity {
    /*
    private lateinit var recentProductDataBase: ShoppingCartDataBase
    private lateinit var recentProductDao: RecentProductDao

    @Before
    fun setUp() {
        recentProductDataBase =
            Room.databaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ShoppingCartDataBase::class.java,
                "recent_products",
            ).build()
        recentProductDataBase.clearAllTables()
        recentProductDao = recentProductDataBase.recentProductDao()
    }

    @After
    fun tearDown() {
        recentProductDataBase.clearAllTables()
        recentProductDataBase.close()
    }

    @Test
    fun `상품_id에_맞는_최근_상품을_불러온다`() {
        // given
        val recentProductEntity = RecentProductEntity(productId = 0, seenDateTime = LocalDateTime.of(2024, 5, 25, 3, 30))
        recentProductDao.insert(recentProductEntity)

        // when
        val actual = recentProductDao.findOrNull(productId = 0)

        // then
        assertThat(actual).isNotNull
        assertThat(actual?.productId).isEqualTo(0)
        assertThat(actual?.seenDateTime).isEqualTo(LocalDateTime.of(2024, 5, 25, 3, 30))
    }

    @Test
    fun `최근_본_상품_5개를_가져온다`() {
        // given
        val recentProductEntities =
            List(10) {
                RecentProductEntity(productId = it, seenDateTime = LocalDateTime.of(2024, 5, 25, 3, it))
            }
        recentProductDao.insertAll(recentProductEntities)

        // when
        val actual = recentProductDao.findRange(5)

        // then
        val expectedProductId = listOf(9L, 8L, 7L, 6L, 5L)
        assertThat(actual).hasSize(5)
        assertThat(actual.map { it.productId }).isEqualTo(expectedProductId)
    }

    @Test
    fun `최근_본_상품을_저장한다`() {
        // given
        val recentProductEntity = RecentProductEntity(productId = 0, seenDateTime = LocalDateTime.of(2024, 5, 25, 3, 30))

        // when
        recentProductDao.insert(recentProductEntity)

        // then
        val actual = recentProductDao.findOrNull(productId = 0)
        assertThat(actual).isNotNull
        assertThat(actual?.productId).isEqualTo(0)
        assertThat(actual?.seenDateTime).isEqualTo(LocalDateTime.of(2024, 5, 25, 3, 30))
    }

    @Test
    fun `상품_id에_맞는_최근_본_상품의_시간을_변경한다`() {
        // given
        val recentProductEntity = RecentProductEntity(productId = 0, seenDateTime = LocalDateTime.of(2024, 5, 25, 3, 30))
        recentProductDao.insert(recentProductEntity)

        // when
        recentProductDao.update(productId = 0, seenDateTime = LocalDateTime.of(2024, 5, 25, 4, 0))

        // then
        val actual = recentProductDao.findOrNull(productId = 0)
        assertThat(actual).isNotNull
        assertThat(actual?.seenDateTime).isEqualTo(LocalDateTime.of(2024, 5, 25, 4, 0))
    }

    private fun RecentProductDao.insertAll(recentProductEntities: List<RecentProductEntity>) {
        recentProductEntities.forEach { insert(it) }
    }

     */
}