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
//	 * 2日分のProgram情報が書かれているXMLを読み込む
//	 * 	1日目：2番組
//	 * 	2日目：1番組
//	 */
//	@LargeTest
//	public void testParse_StandardProgramList_01(){
//		// Test dataを読み込んでinputStreamを作る
//		Context c = getInstrumentation().getContext();
//		Resources r = c.getResources();
//		InputStream source = r.openRawResource(R.raw.test_program_list_01);
//
//		mTarget = new ProgramListParser(source, "UTF-8");
//		
//		// Parserにtest dataを読み込ませる
//		List<Program> result = mTarget.parse();
//		
//		// Nullは返ってこないはず。
//		assertNotNull(result);
//		
//		// 結果を確認。
//		// dummy_program_listには3つの番組がかかれている
//		assertEquals(3, result.size());
//	
//		// 各Programのparse結果の確認
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
