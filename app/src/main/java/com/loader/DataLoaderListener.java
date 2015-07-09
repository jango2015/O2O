package com.loader;

import android.view.View;

/**
 * 数据加载监听器
 * 用于回调处理异步获取到的数据，填充数据到且更新多个View的方式
 */
public interface DataLoaderListener {

	void fillDataInView(Object result, View view);

}
