package cn.com.topnetwork.dxd.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil extends StringUtils {

    /**
     * 驼峰转下划线
     * @param para
     * @return
     */
    public static String humpToUnderline(String para){
        StringBuilder sb = new StringBuilder(para);
        int temp=0;
        if(!para.contains("_")){
            for(int i=0;i<para.length();++i){
                if(Character.isUpperCase(para.charAt(i))){
                    sb.insert(i+temp,"_");
                    ++temp;
                }
            }
        }

        return sb.toString().toUpperCase();
    }


    public static void main(String[] args) {
        System.out.println(humpToUnderline("paramHh"));
    }
}
