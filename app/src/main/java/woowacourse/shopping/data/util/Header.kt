package woowacourse.shopping.data.util

import okhttp3.MediaType.Companion.toMediaType

enum class Header {
    AUTHORIZATION,
    CONTENT_TYPE;

    companion object {
        val CONTENT_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

        fun of(header: Header): String = when (header) {
            AUTHORIZATION -> "Authorization"
            CONTENT_TYPE -> "Content-Type"
        }
    }
}
