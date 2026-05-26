@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.repository

import android.content.SharedPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.preference.SharedPreferencesNotificationSettingRepository

class SharedPreferencesNotificationSettingRepositoryTest {
    @Test
    fun `기본값은 미결제 알림 비활성화다`() {
        val repository = SharedPreferencesNotificationSettingRepository(FakeSharedPreferences())

        assertEquals(false, repository.isUnpaidNotificationEnabled())
    }

    @Test
    fun `미결제 알림 설정값을 SharedPreferences에 저장한다`() {
        val repository = SharedPreferencesNotificationSettingRepository(FakeSharedPreferences())

        repository.setUnpaidNotificationEnabled(true)

        assertEquals(true, repository.isUnpaidNotificationEnabled())
        assertEquals(true, repository.unpaidNotificationEnabled.value)
    }

    @Test
    fun `저장소를 다시 생성해도 SharedPreferences에 저장된 설정을 유지한다`() {
        val sharedPreferences = FakeSharedPreferences()
        val firstRepository = SharedPreferencesNotificationSettingRepository(sharedPreferences)

        firstRepository.setUnpaidNotificationEnabled(true)

        val secondRepository = SharedPreferencesNotificationSettingRepository(sharedPreferences)
        assertEquals(true, secondRepository.isUnpaidNotificationEnabled())
    }

    private class FakeSharedPreferences : SharedPreferences {
        private val values = mutableMapOf<String, Any?>()

        override fun getAll(): MutableMap<String, *> = values

        override fun getString(
            key: String?,
            defValue: String?,
        ): String? = values[key] as? String ?: defValue

        override fun getStringSet(
            key: String?,
            defValues: MutableSet<String>?,
        ): MutableSet<String>? =
            @Suppress("UNCHECKED_CAST")
            (values[key] as? MutableSet<String>)
                ?: defValues

        override fun getInt(
            key: String?,
            defValue: Int,
        ): Int = values[key] as? Int ?: defValue

        override fun getLong(
            key: String?,
            defValue: Long,
        ): Long = values[key] as? Long ?: defValue

        override fun getFloat(
            key: String?,
            defValue: Float,
        ): Float = values[key] as? Float ?: defValue

        override fun getBoolean(
            key: String?,
            defValue: Boolean,
        ): Boolean = values[key] as? Boolean ?: defValue

        override fun contains(key: String?): Boolean = values.containsKey(key)

        override fun edit(): SharedPreferences.Editor = FakeEditor()

        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        }

        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        }

        private inner class FakeEditor : SharedPreferences.Editor {
            override fun putString(
                key: String?,
                value: String?,
            ): SharedPreferences.Editor {
                values[key.orEmpty()] = value
                return this
            }

            override fun putStringSet(
                key: String?,
                values: MutableSet<String>?,
            ): SharedPreferences.Editor {
                this@FakeSharedPreferences.values[key.orEmpty()] = values
                return this
            }

            override fun putInt(
                key: String?,
                value: Int,
            ): SharedPreferences.Editor {
                values[key.orEmpty()] = value
                return this
            }

            override fun putLong(
                key: String?,
                value: Long,
            ): SharedPreferences.Editor {
                values[key.orEmpty()] = value
                return this
            }

            override fun putFloat(
                key: String?,
                value: Float,
            ): SharedPreferences.Editor {
                values[key.orEmpty()] = value
                return this
            }

            override fun putBoolean(
                key: String?,
                value: Boolean,
            ): SharedPreferences.Editor {
                values[key.orEmpty()] = value
                return this
            }

            override fun remove(key: String?): SharedPreferences.Editor {
                values.remove(key)
                return this
            }

            override fun clear(): SharedPreferences.Editor {
                values.clear()
                return this
            }

            override fun commit(): Boolean = true

            override fun apply() {
            }
        }
    }
}
