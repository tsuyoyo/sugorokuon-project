package tsuyogoro.sugorokuon.viewflow;

import android.test.InstrumentationTestCase;

public class UStationInfoManagerTest extends InstrumentationTestCase {

	private StationInfoViewFlow mTarget;

	@Override
	protected void setUp() throws Exception {
		super.setUp();	
	}

	@Override
	protected void tearDown() throws Exception {
		mTarget = null;
		super.tearDown();
	}
	
	/**
	 * Station�f�[�^���X�V���Ă݂�B
	 * �X�V����肭�����Ă��鎖�ƁALogo�̃f�[�^�������ƃ_�E�����[�h����Ă��邱�Ƃ�check�B
	 * 
	 */
//	@LargeTest
//	public void test_UpdateStationData_01() {
//		mTarget = StationInfoViewFlow.getInstance(getInstrumentation().getContext());
//		assertNotNull(mTarget);
//		
//		// �X�V���Ă݂�
//		Area[] targetAreaArray = new Area[] { Area.TOKYO, Area.CHIBA, Area.TOCHIGI, Area.SAITAMA, Area.KANAGAWA, Area.GUNMA };
//		mTarget.updateStationData(targetAreaArray, LogoSize.XSMALL, new DefaultHttpClient());
//
//		// ������stationInfo���X�V����Ă��邩�H
//		List<Station> stationInfo = mTarget.getStationInfo();
//		assertTrue(0 < stationInfo.size());
//
//		// �S�Ă�station�ɑ΂��āA������logo��download���ꂽ���Ƃ��`�F�b�N�B
//		for(Station s : stationInfo) {
//			Bitmap b = mTarget.getLogo(s.id);
//			assertNotNull(b);
//			
//			// XSMALL��105 x 33���炢
//			assertTrue( (30 < b.getHeight()) && (40 > b.getHeight()));
//			assertTrue( (100 < b.getWidth()) && (110 > b.getWidth()));
//		}
//	}
	
}
