package woowacourse.shopping.utils

object ServerURL {
    var url: String = ""
        set(value) {
            field = value.removeSuffix("/")
        }
}
