/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.googleanalytics;

/**
 * Recommendのキーワードを集計するクラス
 *
 */
public class RecommendWordsSender {

    /**
     * TODO :
     * - 大文字小文字を整える
     * - 各keywordを別々の集計ができるようにする (今は , 区切りで全部送ってしまっている)
     */
    public static void send() {
//        List<String> recommends = RecommendWordPreference.getKeyWord(mContext);
//        String label = "";
//        for(String l : recommends) {
//            if(l!=null && 0 < l.length()) {
//                label += l + " , ";
//            }
//        }
//        EasyTracker.getInstance().setContext(mContext);
//        EasyTracker.getTracker().trackEvent(
//                mContext.getText(R.string.ga_event_category_program_update).toString(),
//                mContext.getText(R.string.ga_event_action_download_from_web).toString(),
//                label, null);
    }
}
