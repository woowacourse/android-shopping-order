package woowacourse.shopping.data.respository.card

import woowacourse.shopping.data.model.CardEntity

object CardDAO {
    private val cards = listOf(
        CardEntity(
            id = 1L,
            name = "우테코은행",
            number = "2023-0207-1515-3434",
            cvc = 425
        ),
    )

    fun getCards(): List<CardEntity> = cards
}
