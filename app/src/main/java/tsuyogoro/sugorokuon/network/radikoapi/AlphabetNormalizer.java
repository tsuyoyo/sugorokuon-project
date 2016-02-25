/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

class AlphabetNormalizer {

    private static char[] SIGNS = {
            '！', '＃', '＄', '％', '＆', '（', '）', '＊', '＋', '，', '．', '／',
            '：', '；', '＜', '＝', '＞', '？', '＠', '［', '］', '＾', '＿', '｛', '｜', '｝'
    };

    private static boolean isZenkakuSign(char c) {
        boolean res = false;
        for (char sign : SIGNS) {
            if (sign == c) {
                res = true;
                break;
            }
        }
        return res;
    }

    // 半角のkeywordを全角にする。
    public static String zenkakuToHankaku(String value) {

        StringBuilder sb = new StringBuilder(value);

        int diffZenkakuAndHankaku = 'Ａ' - 'A';

        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);

            if ((c >= 'ａ' && c <= 'ｚ') || (c >= 'Ａ' && c <= 'Ｚ') || (c >= '１' && c <= '９')) {
                sb.setCharAt(i, (char) (c - diffZenkakuAndHankaku));
            } else if (c == '\u3000') {
                sb.setCharAt(i, ' ');
            } else if (isZenkakuSign(c)) {
                sb.setCharAt(i, (char) (c - diffZenkakuAndHankaku));
            }
        }

        value = sb.toString();

        return value;
    }

}
