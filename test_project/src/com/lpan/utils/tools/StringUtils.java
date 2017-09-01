package com.lpan.utils.tools;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    private StringUtils() {

    }

    private static final String EMPTY = "";

    /**
     * 判断是否为null或空字符串。如果不为null，在判断是否为空字符串之前会调用trim()。
     * 
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().equals(EMPTY);
    }

    /**
     * 消除html标签。
     * 
     * @param src
     * @return
     */
    public static String escapeHtml(String src) {
        if (src == null)
            return null;
        return src.replaceAll("<[a-zA-Z/][.[^<]]*>", "");
    }

    /**
     * 消除html特殊标记。如&nbsp;
     * 
     * @param src
     * @return
     */
    public static String escapeBlank(String src) {
        if (src == null)
            return null;
        return src.replaceAll("&[a-zA-Z]+;", "");
    }

    /**
     * 消除img标签
     * 
     * @param src
     * @return
     */
    public static String escapeImg(String src) {
        if (src == null)
            return null;
        return src.replaceAll("<img.*/>", "");
    }

    /**
     * 消除html标签和特殊标记
     * 
     * @param src
     * @return
     */
    public static String escapeHtmlAndBlank(String src) {
        return escapeBlank(escapeHtml(src));
    }

    /**
     * 复制字符串
     * 
     * @param src
     *            源字符串
     * @param times
     *            复制次数
     * @return
     */
    public static String repeat(String src, int times) {
        StringBuffer buffer = new StringBuffer(src);
        for (int i = 0; i < times - 1; i++)
            buffer.append(src);
        return buffer.toString();
    }

    public static String[] printSegmentation(String str, int numberOfCharacters) {
        double number0 = str.length() / numberOfCharacters;
        int quantity;
        if (str.length() % numberOfCharacters == 0)
            quantity = (int) number0;
        else
            quantity = (int) (number0 + 1);
        String[] results = new String[quantity];
        for (int i = 0; i < quantity; i++) {
            String str0;
            if (i == quantity - 1)
                str0 = str;
            else {
                str0 = str.substring(0, numberOfCharacters);
                str = str.substring(numberOfCharacters);
            }
            results[i] = str0;
        }
        return results;
    }

    /**
     * 去掉脚本信息
     * 
     * @param str
     * @return
     */
    public static String removeScript(String str) {
        if (!StringUtils.isNullOrEmpty(str)) {
            Pattern p = Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher = p.matcher(str);
            return matcher.replaceAll("");
        }
        return null;
    }

    /**
     * 转义html特殊标签
     * 
     * @param src
     * @return
     */
    public static String escapeSpecialLabel(String src) {
        if (isNullOrEmpty(src))
            return null;
        return src.replaceAll("&", "&amp;").replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;")
                .replaceAll("\r\n", "<br/>").replaceAll("\n", "<br/>").replaceAll(" ", "&nbsp;")
                .replaceAll("\"", "&quot;");
    }

/**
	 * 反转义html特殊标签
	 * @author xujianguo
	 * @param processBracket 是否处理尖括号'<' 和'>'
	 */
    public static String unEscapeSpecialLabel(String src, boolean processBracket) {
        if (isNullOrEmpty(src))
            return null;
        src = src.replaceAll("(?i)&amp;", "&").replaceAll("(?i)<br\\s*/?\\s*>", "\r\n").replaceAll("(?i)&nbsp;", " ")
                .replaceAll("(?i)&quot;", "\"");
        if (processBracket)
            src = src.replaceAll("(?i)&lt;", "\\<").replaceAll("(?i)&gt;", "\\>");
        return src;
    }

    public static String escapeSpecialChar(String str) {
        str = escapeHtml(escapeBlank(str));
        // String
        // regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_ 《》]";
        String regEx = "[^a-zA-Z0-9\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String removeParameter(String queryString, String parameter) {
        StringBuffer buffer = new StringBuffer();
        String[] parameterPairs = queryString.split("&");
        for (int i = 0; i < parameterPairs.length; i++) {
            String[] kvs = parameterPairs[i].split("=");
            if (kvs[0].equals(parameter))
                continue;
            buffer.append(kvs[0]);
            if (kvs.length == 2)
                buffer.append("=").append(kvs[1]);
            buffer.append("&");
        }
        if (buffer.length() > 0)
            buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    /**
     * 按字节码删除字符串<br/>
     * 如果开始的字节索引是双字节的字符的中间位将被截取<br/>
     * String str = "零壹贰叁肆";System.out.println(deleteByteLength(st,3,6));//零叁肆<br/>
     * 如果结束的字节索引是双字节的字符的中间位将不被截取<br/>
     * String str = "零壹贰叁肆";System.out.println(deleteByteLength(st,2,7));//零叁肆<br/>
     * 
     * @author xujianguo
     * @param inputSrc
     *            被截取的字符串
     * @param startByteIndex
     *            开始的字节索引
     * @param endByteIndex
     *            结束的字节索引
     * @return
     */
    public static String deleteByteLength(String inputSrc, int startByteIndex, int endByteIndex) {
        StringBuilder sb = new StringBuilder(inputSrc);
        int inputByteLength = inputSrc.getBytes().length;
        if (startByteIndex < 0)
            startByteIndex = 0;
        if (endByteIndex > inputByteLength)
            endByteIndex = inputByteLength;
        char[] inputChars = inputSrc.toCharArray();
        int startByteLength = 0;
        int startIndex = 0;
        for (Character c : inputChars) {
            int charByteLength = c.toString().getBytes().length;
            startByteLength += charByteLength;
            if (startByteLength > startByteIndex)
                break;
            startIndex++;
        }
        int endByteLength = 0;
        int endIndex = 0;
        for (Character c : inputChars) {
            int charByteLength = c.toString().getBytes().length;
            endByteLength += charByteLength;
            if (endByteLength > endByteIndex)
                break;
            endIndex++;
        }
        return sb.delete(startIndex, endIndex).toString();
    }

    /**
     * 判断是否是中文
     * 
     * @return
     */
    public static boolean isChinese(char c) {
        return String.valueOf(c).getBytes().length == 2;
    }

    /**
     * 判断字符串中是否包含中文
     * 
     * @return
     */
    public static boolean containsChinese(String str) {
        if (isNullOrEmpty(str))
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (isChinese(str.charAt(i)))
                return true;
        }
        return false;
    }


    /**
     * 切去字符串长度
     * 
     * @param value
     * @param length
     * @param suffix 后缀表示
     * @return
     */
    public static String replace(String value, int length, String suffix) {
        String str = "";
        if (isNullOrEmpty(value)) {
            return str;
        }
        if (length > 0 && length < value.length()) {
            str = value.substring(0, length);
            if (!isNullOrEmpty(suffix)) {
                str += suffix;
            }
        } else {
            str = value;
        }

        return str;
    }
    /** 
     * 判断字符串是否是整数 
     */  
    public static boolean isInteger(String value) {  
        try {  
            Long.parseLong(value);  
            return true;  
        } catch (NumberFormatException e) {  
            //e.printStackTrace();
            return false;  
        }  
    }  
     
    /** 
     * 判断字符串是否是浮点数 
     */  
    public static boolean isDouble(String value) {  
        try {  
            Double.parseDouble(value);  
            if (value.contains("."))  
                return true;  
            return false;  
        } catch (NumberFormatException e) {  
            //e.printStackTrace();
            return false;  
        }  
    }  
     
    /** 
     * 判断字符串是否是数字 
     */  
    public static boolean isNumber(String value) {  
        return isInteger(value) || isDouble(value);  
    }    
    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    /**
     * <p>Checks if a String is not empty ("") and not null.</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }
    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从字符串开始位置截取字符串 中文算两个
     * @param str 原字符串
     * @param length 截取字符串长度
     * @return 截取字符串
     */
    public static String getSubStringFromStart(String str, int length) {
        int count = 0;
        int offset = 0;
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] > 256) {
                count += 2;
                offset = 2;
            } else {
                count++;
                offset = 1;
            }
            if (count == length) {
                return str.substring(0, i+1);
            }
            if (count == length + 1 && offset == 2) {
                return str.substring(0, i);
            }
        }
        return "";
    }

    /**
     * 计算字符串长度，中文算两个
     * @param str 字符串
     * @return 字符串长度
     */
    public static int countStrLength(String str){
        int count = 0;
        char[] c = str.toCharArray();
        for (int i = c.length-1; i > 0; i--) {
            if (c[i] > 256) {
                count += 2;
            } else {
                count++;
            }
        }
        return count;
    }

    /**
     * 从字符串尾部截取字符串 中文算两个
     * @param str 原字符串
     * @param length 截取字符串长度
     * @return 截取字符串
     */
    public static String getSubStringFromEnd(String str, int length) {
        int count = 0;
        int offset = 0;
        char[] c = str.toCharArray();
        for (int i = c.length-1; i > 0; i--) {
            if (c[i] > 256) {
                count += 2;
                offset = 2;
            } else {
                count++;
                offset = 1;
            }
            if (count == length) {
                return str.substring(i, c.length);
            }
            if ((count == length + 1 && offset == 2)) {
                return str.substring(i+1, c.length);
            }
        }
        return "";
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "A张三一BCDF阿萨德法师打发士大夫a";
        String subStr1 = StringUtils.getSubStringFromStart(str, 4);
        String subStr2 = StringUtils.getSubStringFromEnd(str, 4);
        System.out.println("subStr1:"+subStr1);
        System.out.println("subStr2:"+subStr2);
    }
}
