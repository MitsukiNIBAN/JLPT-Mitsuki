package com.mitsuki.jlpt.model

import android.content.SharedPreferences
import com.mitsuki.jlpt.BuildConfig
import com.mitsuki.jlpt.app.constants.*
import com.mitsuki.jlpt.app.int
import com.mitsuki.jlpt.app.kind.Kind
import com.mitsuki.jlpt.app.tts.TTSFactory
import com.mitsuki.jlpt.base.SingletonHolderSingleArg

class SPRepository private constructor(sp: SharedPreferences) {

    //tts类型
    var ttsKind: Int by sp.int(TTS_KIND, TTSFactory.NATIVE)

    //词库版本
    var wordVersion: Int by sp.int(WORD_VERSION, BuildConfig.VERSION_CODE)

    //请求到版本临时存储
    var tempWordVersion: Int by sp.int(WORD_VERSION_TEMP, BuildConfig.VERSION_CODE)

    //展示的词库
    var wordKind: Int by sp.int(WORD_KIND, Kind.ALL)

    //词库范围
    var testingKindRange: Int by sp.int(TESTING_KIND_RANGE, Kind.ALL)

    //词范围(全部，仅隐藏内容，仅不隐藏内容)
    var testingDisplayRange: Int by sp.int(TESTING_DISPLAY_RANGE, Kind.TESTING_DISPLAY_ALL)

    companion object : SingletonHolderSingleArg<SPRepository, SharedPreferences>(::SPRepository)
}