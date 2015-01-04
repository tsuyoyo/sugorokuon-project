/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.constant;

import java.util.ArrayList;
import java.util.List;

import tsuyogoro.sugorokuon.R;

/**
 * �����܂Ƃ߂Đݒ肷��Ƃ��Ɏg���A�n��B
 *
 */
//�k�C���n���@�k�C��
//�E���k�n���@�X���A��茧�A�{�錧�A�H�c���A�R�`���A������
//�E�֓��n���@��錧�A�Ȗ،��A�Q�n���A��ʌ��A��t���A�����s�A�_�ސ쌧
//�E�����n���@�V�����A�x�R���A�ΐ쌧�A���䌧�A�R�����A���쌧�A�򕌌��A�É����A���m��
//�E�ߋE�n���@�O�d���A���ꌧ�A���s�{�A���{�A���Ɍ��A�ޗǌ��A�a�̎R��
//�E�����n���@���挧�A�������A���R���A�L�����A�R����
//�E�l���n���@�������A���쌧�A���Q���A���m��
//�E��B�E����n���@�������A���ꌧ�A���茧�A�F�{���A�啪���A�{�茧�A���������A���ꌧ
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
