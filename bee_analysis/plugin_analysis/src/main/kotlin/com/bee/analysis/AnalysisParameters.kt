package com.bee.analysis

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 *
 * @author petterp
 */
interface AnalysisParameters : InstrumentationParameters {
    @get:Input
    val buildType: Property<String>
}