package org.angmarch.views;

import android.content.Context;
import android.util.Log;

import java.util.List;

/*
 * Copyright (C) 2015 Angelo Marchesin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class NiceSpinnerAdapter<T> extends NiceSpinnerBaseAdapter {

    private final List<T> items;

    NiceSpinnerAdapter(
            Context context,
            List<T> items,
            int textColor,
            int backgroundSelector,
            SpinnerTextFormatter spinnerTextFormatter,
            PopUpTextAlignment horizontalAlignment
    ) {
        super(context, textColor, backgroundSelector, spinnerTextFormatter, horizontalAlignment);
        this.items = items;
    }

    @Override
    public int getCount() {
        Log.e("android", "---" + items.size());
        return items.size();
    }

    @Override
    public T getItem(int position) {
//        if (position >= selectedIndex) {
//            return items.get(position + 1);
//        } else {
        Log.e("android", "---1---"+position+"---"+selectedIndex);
        return items.get(position);
//        }
    }

    @Override
    public T getItemInDataset(int position) {
        Log.e("android", "---2---"+position+"---"+selectedIndex);
        return items.get(position);
    }
}