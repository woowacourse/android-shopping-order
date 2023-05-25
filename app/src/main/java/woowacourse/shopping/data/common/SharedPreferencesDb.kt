package woowacourse.shopping.data.common

interface SharedPreferencesDb {
    fun getString(key: String, defValue: String): String

    fun setString(key: String, str: String)
}
