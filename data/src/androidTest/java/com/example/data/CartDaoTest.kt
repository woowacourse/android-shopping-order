package com.example.data

import com.example.data.datasource.local.room.dao.CartDao

class CartDaoTest {
    private lateinit var shoppingCartDataBase: com.example.data.datasource.local.room.ShoppingCartDataBase
    private lateinit var cartDao: CartDao

    /*
    @Before
    fun setUp() {
        shoppingCartDataBase =
            Room.databaseBuilder(
                ApplicationProvider.getApplicationContext(),
                com.example.data.datasource.local.room.ShoppingCartDataBase::class.java,
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
        val cartItemEntity = CartItemEntity(productId = 0L, quantity = com.example.domain.model.Quantity(
            10
        )
        )

        // when
        cartDao.insert(cartItemEntity)

        // then
        val actual = cartDao.find(productId = 0)
        assertThat(actual.productId).isEqualTo(0L)
        assertThat(actual.quantity).isEqualTo(com.example.domain.model.Quantity(10))
    }

    @Test
    fun `카트_아이템의_수량을_변경한다`() {
        // given
        val cartItemEntity = CartItemEntity(productId = 0L, quantity = com.example.domain.model.Quantity(
            10
        )
        )
        cartDao.insert(cartItemEntity)

        // when
        cartDao.changeQuantity(productId = 0L, quantity = com.example.domain.model.Quantity(1))

        // then
        val actual = cartDao.find(productId = 0L)
        assertThat(actual.quantity).isEqualTo(com.example.domain.model.Quantity(1))
    }

    @Test
    fun `카트_아이템을_삭제한다`() {
        // given
        val cartItemEntity = CartItemEntity(productId = 0L, quantity = com.example.domain.model.Quantity(
            10
        )
        )
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
        val cartItemEntity = CartItemEntity(productId = 0L, quantity = com.example.domain.model.Quantity(
            10
        )
        )
        val cartItemId = cartDao.insert(cartItemEntity)

        // when
        val actual = cartDao.find(productId = 0L)

        // then
        assertThat(actual.id).isEqualTo(cartItemId)
        assertThat(actual.productId).isEqualTo(0L)
        assertThat(actual.quantity).isEqualTo(com.example.domain.model.Quantity(10))
    }

    @Test
    fun `카트_아이템이_15개_저장되어_있고_첫_페이지를_불러오면_5개가_반환된다`() {
        // given
        val cartItemEntities = List(10) { CartItemEntity(productId = 0L, quantity = com.example.domain.model.Quantity(
            10
        )
        ) }
        cartDao.insertAllCartItem(cartItemEntities)

        // when
        val actual = cartDao.findRange(0, 5)

        // then
        assertThat(actual).hasSize(5)
    }

    @Test
    fun `카트_아이템이_3개_저장되어_있고_첫_페이지를_불러오면_3개가_반환된다`() {
        // given
        val cartItemEntities = List(3) { CartItemEntity(productId = 0L, quantity = com.example.domain.model.Quantity(
            10
        )
        ) }
        cartDao.insertAllCartItem(cartItemEntities)

        // when
        val actual = cartDao.findRange(0, 5)

        // then
        assertThat(actual).hasSize(3)
    }

    @Test
    fun `카트_아이템의_총_개수를_반환한다`() {
        // given
        val cartItemEntities = List(22) { CartItemEntity(productId = 0L, quantity = com.example.domain.model.Quantity(
            10
        )
        ) }
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

     */
}
