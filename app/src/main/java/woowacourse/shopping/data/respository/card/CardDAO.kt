package woowacourse.shopping.data.respository.card

import woowacourse.shopping.data.model.CardEntity

object CardDAO {
    private val cards = listOf(
        CardEntity(
            id = 1L,
            name = "NH농협은행",
            number = "4043-0304-1299-4949",
            cvc = 123
        )
    )

    fun getCards(): List<CardEntity> = cards
}
