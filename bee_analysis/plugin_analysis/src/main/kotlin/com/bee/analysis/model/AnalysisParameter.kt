package com.bee.analysis.model

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 *
 * @author petterp
 */
interface AnalysisParameter : InstrumentationParameters {

    @get:Input
    val packages: Property<Array<String>>
}
