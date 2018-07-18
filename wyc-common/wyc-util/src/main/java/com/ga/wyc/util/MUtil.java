package com.ga.wyc.util;

import com.ga.wyc.domain.bean.ValidException;
import com.ga.wyc.domain.enums.NetType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MUtil {

    public final static String REGX_PHONE="^[1][3,4,5,6,7,8,9][0-9]{9}$";
    public final static String REGX_XY="^[0-9]{1,10}+(.[0-9]{6})?$";
    /*
   1、移动号段有134,135,136,137, 138,139,147,150,151, 152,157,158,159,178,182,183,184,187,188。
   2、联通号段有130，131，132，155，156，185，186，145，176。
   3、电信号段有133，153，177.173，180，181，189。
   */
    private final  String YD = "^[1]{1}(([3]{1}[4-9]{1})|([5]{1}[012789]{1})|([8]{1}[23478]{1})|([4]{1}[7]{1})|([7]{1}[8]{1}))[0-9]{8}$";
    private final  String LT = "^[1]{1}(([3]{1}[0-2]{1})|([5]{1}[56]{1})|([8]{1}[56]{1})|([4]{1}[5]{1})|([7]{1}[6]{1}))[0-9]{8}$";
    private final  String DX = "^[1]{1}(([3]{1}[3]{1})|([5]{1}[3]{1})|([8]{1}[09]{1})|([7]{1}[37]{1}))[0-9]{8}$";


    /**
     *  判断运营商
     * **/
    public  NetType matchNum(String mobPhnNum) {
        // 判断手机号码是否是11位
        if (mobPhnNum.length() == 11) {
            // 判断手机号码是否符合中国移动的号码规则
            if (mobPhnNum.matches(YD)) {
               return NetType.YD;
            }
            // 判断手机号码是否符合中国联通的号码规则
            else if (mobPhnNum.matches(LT)) {
                return NetType.LT;
            }
            // 判断手机号码是否符合中国电信的号码规则
            else if (mobPhnNum.matches(DX)) {
                return NetType.DX;
            }
            // 都不合适 未知
            else {
                return NetType.NO;
            }
        }else {// 不是11位
            return NetType.NO;
        }
    }


    /**
     * MD5
     * @param md5
     * @return 加密字符串
     */
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * @author luqi
     * 是否为电话
     */
    public boolean isTelphone(String telphone) {
        return isMatchReg(telphone, REGX_PHONE);
    }

    /**
     *  是否为规范坐标
     * */
    public boolean isLocationXY(BigDecimal ... xy){
        for(BigDecimal loc:xy){
           String locStr=loc.toString();
           if(!isMatchReg(locStr,REGX_XY)){
               return false;
           }
        }
        return true;
    }

    /**
     * @author luqi
     * 正则表达式是否匹配
     */
    public boolean isMatchReg(String msg, String reg) {
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(msg);
        return matcher.matches();
    }


    public String UUID(){
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }

    /**检查参数*/
    public void checkParam(BindingResult result){
        if(result.hasErrors()){
            throw  new ValidException(result.getFieldError().getDefaultMessage());
        }
    }
}
