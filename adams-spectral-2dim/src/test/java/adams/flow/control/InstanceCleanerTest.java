/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * InstanceCleanerTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.data.cleaner.instance.MinMax;
import adams.db.JdbcUrl;
import adams.env.Environment;
import adams.flow.AbstractSpectrumFlowTest;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.standalone.DatabaseConnection;
import adams.flow.transformer.WekaFileReader;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;

/**
 * Tests the InstanceCleaner actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1865 $
 */
public class InstanceCleanerTest
  extends AbstractSpectrumFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public InstanceCleanerTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile-ok.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-rejected.txt");

    m_TestHelper.copyResourceToTmp("clay.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile-ok.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-rejected.txt");

    m_TestHelper.deleteFileFromTmp("clay.arff");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    DatabaseConnection dbcon = new DatabaseConnection();
    dbcon.setURL(new JdbcUrl(getDatabaseURL()));
    dbcon.setUser(getDatabaseUser());
    dbcon.setPassword(getDatabasePassword());

    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("clay.arff")});

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.INCREMENTAL);

    DumpFile df_ok = new DumpFile();
    df_ok.setAppend(true);
    df_ok.setOutputFile(new TmpFile("dumpfile-ok.txt"));

    DumpFile df_rej = new DumpFile();
    df_rej.setAppend(true);
    df_rej.setOutputFile(new TmpFile("dumpfile-rejected.txt"));

    InstanceCleaner ic = new InstanceCleaner();
    MinMax mm = new MinMax();
    mm.setAttributeName("Clay");
    mm.setMinimum(100.0);
    mm.setMaximum(200.0);
    ic.setCleaner(mm);
    ic.setRejectionMessagesActor(df_rej);

    Flow flow = new Flow();
    flow.setActors(new Actor[]{dbcon, sfs, fr, ic, df_ok});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile-ok.txt"),
	    new TmpFile("dumpfile-rejected.txt")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(InstanceCleanerTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args){
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
