package com.lyc.sample.impl

import com.lyc.appinject.CreateMethod
import com.lyc.appinject.annotations.InjectApiImpl
import com.lyc.appinject.sample.api.IOneToManyApi

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApiImpl(api = IOneToManyApi::class, createMethod = CreateMethod.GET_INSTANCE)
class OneToManyApiImplKt private constructor() : IOneToManyApi {

    companion object {

        private val instance = OneToManyApiImplKt()

        // important!
        @JvmStatic
        fun getInstance(): IOneToManyApi {
            return instance
        }
    }

    override fun logMsg(): String {
        return "I'm OneToManyApiImplKt, created by getInstance()!"
    }
}
