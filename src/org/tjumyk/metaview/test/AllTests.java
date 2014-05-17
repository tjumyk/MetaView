package org.tjumyk.metaview.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 所有测试用例
 * 
 * @author 宇锴
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ TestParseXML.class, TestImportOldMeta.class, TestWriteXML.class})
public class AllTests {

}
