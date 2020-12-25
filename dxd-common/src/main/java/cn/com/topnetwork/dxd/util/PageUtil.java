package cn.com.topnetwork.dxd.util;

import cn.com.topnetwork.dxd.base.param.BasePageParam;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class PageUtil {

    public  static <T extends BasePageParam> void initPage(T param){
        PageHelper.startPage(param.getPageIndex(),param.getPageSize());

        StringBuffer orderBy = new StringBuffer();
        if(MapUtils.isNotEmpty(param.getPageSorts())){
            HashMap<String,String> pageSortMap = param.getPageSorts();
            for(Map.Entry<String,String> entry:pageSortMap.entrySet()){
                String column = StringUtil.humpToUnderline(entry.getKey());
                String val = entry.getValue();
                if("asc".equalsIgnoreCase(val)){
                    orderBy.append(column + " asc,");
                }else if("desc".equalsIgnoreCase(val)){
                    orderBy.append(column + " desc,");
                }
            }
        }
        if(orderBy.length()>0){
            orderBy.deleteCharAt(orderBy.length()-1);
            PageHelper.orderBy(orderBy.toString());
        }
    }
}
