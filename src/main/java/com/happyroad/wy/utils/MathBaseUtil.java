package com.happyroad.wy.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/1/18 19:23
 * @desc 精确加减乘除操作工具类
 */
public class MathBaseUtil {

    private static final int DEF_DIV_SCALE = 10;

    //这个类不能实例化
    private MathBaseUtil() {

    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static String add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).toString();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static String subtract(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);

        BigDecimal b2 = new BigDecimal(v2);

        return b1.subtract(b2).toString();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static String multiply(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).toString();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static String divide(String v1, String v2) {
        return divide(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static String divide(String v1, String v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static String round(String v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
    }


    /**
     * description: java 实现若干个数计算平均值，并 四舍五入模式，保留若干位小数。
     *
     * @param scale 保留小数的个数
     * @param nums  若干个字符数字
     * @return Double
     * @version v1.0
     * @author w
     * @date 2019年10月25日 下午9:53:07
     */
    public static String average( String[] nums,int scale) {

        if (ArrayUtils.isEmpty(nums)) {
            return null;
        }

        double sum  = 0D;

        for (String num : nums) {
            sum += Double.parseDouble(num);
        }
        BigDecimal decimal = BigDecimal.valueOf(sum/nums.length).setScale(scale, RoundingMode.HALF_UP);

        return decimal.toString();

    }

    /**
     * 四舍六入五成双：  BigDecimal.ROUND_HALF_EVEN
     *
     * 　　四舍六入五成双是一种比较精确比较科学的计数保留法，是一种数字修约规则。
     *
     * 　　对于位数很多的近似数，当有效位数确定后，其后面多余的数字应该舍去，只保留有效数字最末一位，这种修约(舍入)规则是"四舍六入五成双"，也即"4舍6入5凑偶"这里"四"是指≤4 时舍去，"六"是指≥6时进上，"五"指的是根据5后面的数字来定，当5后有数时，舍5入1;当5后无有效数字时，需要分两种情况来讲:①5前为奇数，舍5入1;②5前为偶数，舍5不进。(0是偶数)
     *
     * 　　从统计学的角度，"四舍六入五成双"比"四舍五入"要科学，在大量运算时，它使舍入后的结果误差的均值趋于零，而不是像四舍五入那样逢五就入，导致结果偏向大数，使得误差产生积累进而产生系统误差，"四舍六入五成双"使测量结果受到舍入误差的影响降到最低。
     *
     * 　　在实际情况下，大部分交易的手续费是按单笔计算的，每一笔都对应一个手续费。
     *
     * 　　例如费率为千分之三，交易1000元，手续费3元，实际到帐997元。交易255元，手续费 255 * 0.003 = 0.765 元。
     *
     * 　　但是一般做交易时金额都是精确到分的，采用四舍六入五成双方法保留小数点后两位，所以这里手续费是0.76，实际到帐254.24元。
     *
     */
    /**
     * 1、存储计算金额时，最好直接存整数（表示单位为分、厘、毫），然后直接对整数进行加减运算，最后在最终展示的时候，再换算成所需的单位。
     * 2、需要保证精度的运算最好使用BigDecimal类，因为其精度准确，且与其它基本数据类型装换方便。
     * 3、合理利用一些成熟可靠的第三方工具类，可以给数字相关运算带来很大的便利。
     */


}
