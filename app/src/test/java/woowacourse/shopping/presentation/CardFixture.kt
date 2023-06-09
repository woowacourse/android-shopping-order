package woowacourse.shopping.presentation

import woowacourse.shopping.presentation.model.CardModel

object CardFixture {
    fun getFixture(): List<CardModel> {
        return listOf(
            CardModel(
                id = 1L,
                name = "우테코은행",
                number = "2023-0207-1515-3434",
                cvc = 425
            ),
        )
    }
}
