package com.app.weatherforecast.core.test

import java.util.Locale
import java.util.TimeZone
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * [TestRule] that forces the given [Locale]/[TimeZone] and resets it once the test is done
 *
 * @param locale The [Locale] to use in tests, default is [Locale.US]
 * @param timeZone The [TimeZone] to use in tests, default is "EST"
 */
class LocaleRule(
    private val locale: Locale = Locale.US,
    private val timeZone: TimeZone = TimeZone.getTimeZone("EST")
) : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                val originalLocale = Locale.getDefault()
                val originalTimeZone = TimeZone.getDefault()
                try {
                    Locale.setDefault(locale)
                    TimeZone.setDefault(timeZone)
                    base?.evaluate()
                } finally {
                    Locale.setDefault(originalLocale)
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }
    }
}