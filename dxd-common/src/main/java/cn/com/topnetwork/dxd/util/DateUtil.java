/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.topnetwork.dxd.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tby
 * @date 2018-11-08
 */
public class DateUtil {

    /**
     * 年-月-日
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    /**
     * 年-月-日 时:分
     */
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    /**
     * 年-月-日 时:分:秒
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    /**
     * 年-月-日 时:分:秒:毫秒
     */
    public static final String YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.S";

    /**
     * 时:分
     */
    public static final String HH_MM = "HH:mm";
    /**
     * 时:分:秒
     */
    public static final String HH_MM_SS = "HH:mm:ss";
    /**
     * 时:分:秒:毫秒
     */
    public static final String HH_MM_SS_S = "HH:mm:ss:S";


    public static String getDateString(Date date){
        if (date == null){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    public static String getDateTimeString(Date date){
        if (date == null){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

}
