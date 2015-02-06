/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.constant;

import java.util.ArrayList;
import java.util.List;

import tsuyogoro.sugorokuon.R;

/**
 * 県をまとめて設定するときに使う、地域。
 *
 */
//北海道地方　北海道
//・東北地方　青森県、岩手県、宮城県、秋田県、山形県、福島県
//・関東地方　茨城県、栃木県、群馬県、埼玉県、千葉県、東京都、神奈川県
//・中部地方　新潟県、富山県、石川県、福井県、山梨県、長野県、岐阜県、静岡県、愛知県
//・近畿地方　三重県、滋賀県、京都府、大阪府、兵庫県、奈良県、和歌山県
//・中国地方　鳥取県、島根県、岡山県、広島県、山口県
//・四国地方　徳島県、香川県、愛媛県、高知県
//・九州・沖縄地方　福岡県、佐賀県、長崎県、熊本県、大分県、宮崎県、鹿児島県、沖縄県
public enum Region {

	HOKKAIDO(new Area[]{ Area.HOKKAIDO }, R.string.region_hokkaido),
	
	TOHOKU(new Area[]{ Area.AOMORI, Area.IWATE, Area.MIYAAGI, Area.AKITA, 
			Area.YAMAGATA, Area.FUKUSHIMA }, R.string.region_tohoku),
	
	KANTO(new Area[]{ Area.IBARAKI, Area.TOCHIGI, Area.GUNMA, Area.SAITAMA,
			Area.CHIBA, Area.TOKYO, Area.KAGAWA}, R.string.region_kanto),
	
	CHUBU(new Area[]{ Area.NIIGATA, Area.TOYAMA, Area.ISHIKAWA, Area.FUKUI,
			Area.YAMANASHI, Area.NAGANO, Area.GIFU, Area.SHIZUOKA, Area.AICHI },
			R.string.region_chubu),
	
	KINKI(new Area[]{ Area.MIE, Area.SHIGA, Area.KYOTO, Area.OSAKA, Area.HYOGO,
			Area.NARA, Area.WAKAYAMA}, R.string.region_kinki),
	
	CHUGOKU(new Area[]{ Area.TOTTORI, Area.SHIMANE, Area.OKAYAMA, Area.HIROSHIMA, Area.YAMAGUCHI },
			R.string.region_chugoku),
	
	SHIKOKU(new Area[]{ Area.TOKUSHIMA, Area.KAGAWA, Area.EHIME, Area.KOUCHI },
			R.string.region_shikoku),
	
	KYUSHUOKINAWA(new Area[]{ Area.FUKUOKA, Area.SAGA, Area.NAGASAKI, Area.KUMAMOTO,
			Area.OITA, Area.MIYAZAKI, Area.KAGOSHIMA, Area.OKINAWA },
			R.string.region_kyushuokinawa),
	;
	
	public List<Area> areas;
	
	public int strId;
	
	private Region(Area[] areas, int strId) {
		this.areas = new ArrayList<Area>();
		for(Area a : areas) {
			this.areas.add(a);
		}
		this.strId = strId;
	}
	
	
}
