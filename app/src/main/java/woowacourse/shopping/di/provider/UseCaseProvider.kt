package woowacourse.shopping.di.provider

import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase

object UseCaseProvider {
    lateinit var increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase
        private set
    lateinit var decreaseProductQuantityUseCase: DecreaseProductQuantityUseCase
        private set

    fun initIncreaseCartProductQuantityUseCase(useCase: IncreaseCartProductQuantityUseCase) {
        increaseCartProductQuantityUseCase = useCase
    }

    fun initDecreaseProductQuantityUseCase(useCase: DecreaseProductQuantityUseCase) {
        decreaseProductQuantityUseCase = useCase
    }
}
