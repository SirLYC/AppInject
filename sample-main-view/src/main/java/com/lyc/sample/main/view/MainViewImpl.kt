package com.lyc.sample.main.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.lyc.appinject.annotations.InjectApiImpl
import com.lyc.appinject.sample.api.*

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApiImpl(api = IMainView::class)
class MainViewImpl : IMainView {
    override fun createContentView(context: Context): View {
        return TextView(context).apply {
            gravity = Gravity.CENTER
            val singleApi = getSingleApi<ISingleApi>()!!
            append(singleApi.logMsg() + "\n")
            for (impl in getOneToManyApiList<IOneToManyApi>()) {
                append(impl.logMsg() + "\n")
            }
            val getInstanceApi: IGetInstanceApi = getSingleApi()!!
            append(getInstanceApi.logMsg() + "\n")

            val abstractApi = getSingleApi<AbstractApi>()!!
            append(abstractApi.logMsg() + "\n")
        }
    }
}
