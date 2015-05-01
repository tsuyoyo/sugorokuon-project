/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.constants;

import tsuyogoro.sugorokuon.R;

/**
 * 受信エリアのenumクラス。全47都道府県。
 *
 * @author Tsuyoyo
 *
 */
public enum Area {
	HOKKAIDO	("JP1" , "HOKKAIDO JAPAN" , R.string.area_hokkaido),
	AOMORI  	("JP2" , "AOMORI JAPAN"   ,	R.string.area_aomori),
	IWATE		("JP3" , "IWATE JAPAN"    ,	R.string.area_iwate),
	MIYAAGI   	("JP4" , "MIYAGI JAPAN"   ,	R.string.area_miyagi),
	AKITA     	("JP5" , "AKITA JAPAN"    ,	R.string.area_akita),
	YAMAGATA  	("JP6" , "YAMAGATA JAPAN" , R.string.area_yamagata),
	FUKUSHIMA	("JP7" , "FUKUSHIMA JAPAN", R.string.area_fukushima),
	IBARAKI		("JP8" , "IBARAKI JAPAN"  , R.string.area_ibaraki),
	TOCHIGI		("JP9" , "TOCHIGI JAPAN"  , R.string.area_tochigi),
	GUNMA		("JP10" , "GUNMA JAPAN"	  , R.string.area_gunma),
	SAITAMA		("JP11" , "SAITAMA JAPAN" , R.string.area_saitama),
	CHIBA		("JP12" , "CHIBA JAPAN"   , R.string.area_chiba),
	TOKYO		("JP13" , "TOKYO JAPAN"   , R.string.area_tokyo),
	KANAGAWA	("JP14" , "KANAGAWA JAPAN", R.string.area_kanagawa),
	NIIGATA		("JP15" , "NIIGATA JAPAN" , R.string.area_niigata),
	TOYAMA		("JP16" , "TOYAMA JAPAN"  , R.string.area_toyama),
	ISHIKAWA	("JP17" , "ISHIKAWA JAPAN", R.string.area_ishikawa),
	FUKUI		("JP18" , "FUKUI JAPAN"   , R.string.area_fukui),
	YAMANASHI	("JP19" , "YAMANASHI JAPAN",R.string.area_yamanashi),
	NAGANO		("JP20" , "NAGANO JAPAN"  , R.string.area_nagano),
	GIFU		("JP21" , "GIFU JAPAN"	  , R.string.area_gifu),
	SHIZUOKA	("JP22" , "SHIZUOKA JAPAN", R.string.area_shizuoka),
	AICHI		("JP23" , "AICHI JAPAN"   , R.string.area_aichi),
	MIE			("JP24" , "MIE JAPAN"     , R.string.area_mie),
	SHIGA		("JP25" , "SHIGA JAPAN"   , R.string.area_shiga),
	KYOTO		("JP26" , "KYOTO JAPAN"   , R.string.area_kyoto),
	OSAKA		("JP27" , "OSAKA JAPAN"   , R.string.area_osaka),
	HYOGO		("JP28" , "HYOGO JAPAN"   , R.string.area_hyogo),
	NARA		("JP29" , "NARA JAPAN"    , R.string.area_nara),
	WAKAYAMA	("JP30" , "WAKAYAMA JAPAN", R.string.area_wakayama),
	TOTTORI		("JP31" , "TOTTORI JAPAN" , R.string.area_tottori),
	SHIMANE		("JP32" , "SHIMANE JAPAN" , R.string.area_shimane),
	OKAYAMA		("JP33" , "OKAYAMA JAPAN" , R.string.area_okayama),
	HIROSHIMA	("JP34" , "HIROSHIMA JAPAN",R.string.area_hiroshima),
	YAMAGUCHI	("JP35" , "YAMAGUCHI JAPAN",R.string.area_yamaguchi),
	TOKUSHIMA	("JP36" , "TOKUSHIMA JAPAN",R.string.area_tokushima),
	KAGAWA		("JP37" , "KAGAWA JAPAN"  , R.string.area_kagawa),
	EHIME		("JP38" , "EHIME JAPAN"   , R.string.area_ehime),
	KOUCHI		("JP39" , "KOUCHI JAPAN"  , R.string.area_kouchi),
	FUKUOKA		("JP40" , "FUKUOKA JAPAN" , R.string.area_fukuoka),
	SAGA		("JP41" , "SAGA JAPAN"    , R.string.area_saga),
	NAGASAKI	("JP42" , "NAGASAKI JAPAN", R.string.area_nagasaki),
	KUMAMOTO	("JP43" , "KUMAMOTO JAPAN", R.string.area_kumamoto),
	OITA		("JP44" , "OHITA JAPAN"   , R.string.area_oita),
	MIYAZAKI	("JP45" , "MIYAZAKI JAPAN", R.string.area_miyazaki),
	KAGOSHIMA	("JP46" , "KAGOSHIMA JAPAN",R.string.area_kagoshima),
	OKINAWA		("JP47" , "OKINAWA JAPAN" , R.string.area_okinawa)
	;

	public String id;

	public String name;

	public int strId;

	private Area(String _id, String _name, int _strId) {
		id = _id;
		name = _name;
		strId = _strId;
	}
	
}
