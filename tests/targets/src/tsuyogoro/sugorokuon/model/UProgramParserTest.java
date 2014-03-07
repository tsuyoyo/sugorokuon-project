package tsuyogoro.sugorokuon.model;

import java.io.InputStream;
import java.util.List;

import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.test.R;
import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

public class UProgramParserTest extends InstrumentationTestCase {
	
	private ProgramListParser mTarget;

	@Override
	protected void setUp() throws Exception {
		super.setUp();	
	}

	@Override
	protected void tearDown() throws Exception {
		mTarget = null;
		super.tearDown();
	}
	
//	/**
//	 * 2������Program��񂪏�����Ă���XML��ǂݍ���
//	 * 	1���ځF2�ԑg
//	 * 	2���ځF1�ԑg
//	 */
//	@LargeTest
//	public void testParse_StandardProgramList_01(){
//		// Test data��ǂݍ����inputStream�����
//		Context c = getInstrumentation().getContext();
//		Resources r = c.getResources();
//		InputStream source = r.openRawResource(R.raw.test_program_list_01);
//
//		mTarget = new ProgramListParser(source, "UTF-8");
//		
//		// Parser��test data��ǂݍ��܂���
//		List<Program> result = mTarget.parse();
//		
//		// Null�͕Ԃ��Ă��Ȃ��͂��B
//		assertNotNull(result);
//		
//		// ���ʂ��m�F�B
//		// dummy_program_list�ɂ�3�̔ԑg��������Ă���
//		assertEquals(3, result.size());
//	
//		// �eProgram��parse���ʂ̊m�F
//		Program prog01 = result.get(0);
//		assertEquals("20121022050000", prog01.start);
//		assertEquals("20121022085000", prog01.end);
//		assertEquals("Dummy program 01", prog01.title);
//		assertEquals("", prog01.subtitle);
//		assertEquals("PFM01", prog01.personalities);
//		assertEquals("DESC01", prog01.description);
//		assertEquals("Info_01<br>Info_01<br><a href=\"mailto:ohaten@joqr.net\">ohaten@joqr.net</a>", prog01.info);
//		assertEquals("http://web.bayfm.jp/pbm/", prog01.url);
//		
//		
//		
//	}
	
}
