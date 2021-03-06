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
 * PutSampleData.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest;

import adams.data.conversion.JsonToReport;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportJsonUtils;
import adams.data.sampledata.SampleData;
import adams.db.SampleDataF;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * REST plugin for uploading sample data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PutSampleData
  extends AbstractRESTPluginWithDatabaseConnection {

  private static final long serialVersionUID = -826056354423201513L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stores sample data in the database.\n"
      + "Overwrites existing sample data.\n"
      + "Format:\n"
      + ReportJsonUtils.example();
  }

  /**
   * Stores the upload spectrum (in JSON format) in the database.
   *
   * @param id		the sample ID
   * @param content	the spectrum in JSON format
   * @return		the sample ID or error message
   */
  @POST
  @Path("/sampledata/put/{id}")
  @Produces("text/plain")
  public String put(@PathParam("id") String id, String content) {
    Report 		rep;
    SampleData		sd;
    JsonToReport 	conv;
    String		msg;
    SampleDataF sdt;

    initDatabase();
    conv = new JsonToReport();
    conv.setInput(content);
    msg = conv.convert();
    if (msg == null) {
      rep = (Report) conv.getOutput();
      sd  = new SampleData();
      sd.mergeWith(rep);
      sdt = SampleDataF.getSingleton(m_DatabaseConnection);
      if (!sdt.store(id, sd, true, true, (Field[]) sd.getFields().toArray()))
        return "Failed to store sample data under ID '" + id + "':\n" + sd;
      else
	return id;
    }
    else {
      return
	"Failed to parse JSON string:\n"
	  + content
	  + "\n"
	  + "Error message:\n"
	  + msg
	  + "\n"
	  + "Expected format:\n"
	  + ReportJsonUtils.example();
    }
  }
}
