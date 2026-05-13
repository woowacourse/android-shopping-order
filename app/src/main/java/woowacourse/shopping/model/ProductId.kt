package woowacourse.shopping.model

@JvmInline
value class ProductId(
    val value: Long,
) {
    companion object {
        fun fromRemoteId(remoteId: Long): ProductId = ProductId(remoteId)
    }
}
