package com.lyc.appinject;

import com.lyc.appinject.impl.Implementation;
import com.lyc.appinject.impl.ImplementationGetInstance;
import com.lyc.appinject.impl.ImplementationNew;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liu Yuchuan on 2020/1/17.
 */
public class ImplementationFactory {

    private static final Map<String, CreateMethod> createMethodMap;

    private static final String TAG = "ImplementationFactory";

    static {
        Map<String, CreateMethod> map = new HashMap<>();
        for (CreateMethod value : CreateMethod.values()) {
            map.put(value.name(), value);
        }
        createMethodMap = map;
    }


    public static Implementation createImpl(Class<?> clazz, String name) {
        CreateMethod createMethod = createMethodMap.get(name);

        if (createMethod != null) {
            switch (createMethod) {
                case NEW:
                    return new ImplementationNew(clazz);
                case GET_INSTANCE:
                    return new ImplementationGetInstance(clazz);
            }
        } else {
            AppInject.getLogger().e(TAG, "for clazz: " + (clazz == null ? "null" : clazz.getName()) + ", createMethod=null, name=" + name, null);
        }

        return new ImplementationNew(clazz);
    }
}
