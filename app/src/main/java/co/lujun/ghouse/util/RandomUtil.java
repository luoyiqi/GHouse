package co.lujun.ghouse.util;

import android.text.TextUtils;

import java.util.Random;

/**
 * Random Utils
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2012-5-12
 */
public class RandomUtil {

    public static final String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS             = "0123456789";
    public static final String LETTERS             = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CAPITAL_LETTERS     = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CASE_LETTERS  = "abcdefghijklmnopqrstuvwxyz";

    private RandomUtil() {
        throw new AssertionError();
    }

    /**
     * get a fixed-length random string, its a mixture of uppercase, lowercase letters and numbers
     *
     * @param length
     * @return
     * @see RandomUtil#getRandom(String source, int length)
     */
    public static String getRandomNumbersAndLetters(int length) {
        return getRandom(NUMBERS_AND_LETTERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of numbers
     *
     * @param length
     * @return
     * @see RandomUtil#getRandom(String source, int length)
     */
    public static String getRandomNumbers(int length) {
        return getRandom(NUMBERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of uppercase and lowercase letters
     *
     * @param length
     * @return
     * @see RandomUtil#getRandom(String source, int length)
     */
    public static String getRandomLetters(int length) {
        return getRandom(LETTERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of uppercase letters
     *
     * @param length
     * @return
     * @see RandomUtil#getRandom(String source, int length)
     */
    public static String getRandomCapitalLetters(int length) {
        return getRandom(CAPITAL_LETTERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of lowercase letters
     *
     * @param length
     * @return
     * @see RandomUtil#getRandom(String source, int length)
     */
    public static String getRandomLowerCaseLetters(int length) {
        return getRandom(LOWER_CASE_LETTERS, length);
    }

    /**
     * get a fixed-length random string, its a mixture of chars in source
     *
     * @param source
     * @param length
     * @return <ul>
     *         <li>if source is null or empty, return null</li>
     *         <li>else see {@link RandomUtil#getRandom(char[] sourceChar, int length)}</li>
     *         </ul>
     */
    public static String getRandom(String source, int length) {
        return TextUtils.isEmpty(source) ? null : getRandom(source.toCharArray(), length);
    }

    /**
     * get a fixed-length random string, its a mixture of chars in sourceChar
     *
     * @param sourceChar
     * @param length
     * @return <ul>
     *         <li>if sourceChar is null or empty, return null</li>
     *         <li>if length less than 0, return null</li>
     *         </ul>
     */
    public static String getRandom(char[] sourceChar, int length) {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }

        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(sourceChar[random.nextInt(sourceChar.length)]);
        }
        return str.toString();
    }

    /**
     * get random int between 0 and max
     *
     * @param max
     * @return <ul>
     *         <li>if max <= 0, return 0</li>
     *         <li>else return random int between 0 and max</li>
     *         </ul>
     */
    public static int getRandom(int max) {
        return getRandom(0, max);
    }

    /**
     * get random int between min and max
     *
     * @param min
     * @param max
     * @return <ul>
     *         <li>if min > max, return 0</li>
     *         <li>if min == max, return min</li>
     *         <li>else return random int between min and max</li>
     *         </ul>
     */
    public static int getRandom(int min, int max) {
        if (min > max) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        return min + new Random().nextInt(max - min);
    }
}