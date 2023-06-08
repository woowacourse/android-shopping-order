package woowacourse.shopping.data.remote.dto

data class TotalCashDTO(val totalCash: Int?) {
    val isNotNull: Boolean
        get() = totalCash != null
}
