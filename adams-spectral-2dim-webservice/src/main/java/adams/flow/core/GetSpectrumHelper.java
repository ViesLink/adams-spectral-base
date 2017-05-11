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

/**
 * GetSpectrumHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.data.report.AbstractField;
import adams.data.report.Field;
import adams.data.spectrum.SpectrumPoint;

/**
 * Helper class for converting spectra.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2118 $
 */
public class GetSpectrumHelper {

  /**
   * Converts a KNIR spectrum into a Webservice one.
   * 
   * @param input	the KNIR spectrum
   * @return		the Webservice spectrum
   */
  public static nz.ac.waikato.adams.webservice.spectral.get.Spectrum knirToWebservice(adams.data.spectrum.Spectrum input) {
    nz.ac.waikato.adams.webservice.spectral.get.Spectrum	result;
    nz.ac.waikato.adams.webservice.spectral.get.Waves		waves;
    nz.ac.waikato.adams.webservice.spectral.get.Wave		wave;
    nz.ac.waikato.adams.webservice.spectral.get.Properties	props;
    nz.ac.waikato.adams.webservice.spectral.get.Property	prop;
    adams.data.sampledata.SampleData				report;
    
    result = new nz.ac.waikato.adams.webservice.spectral.get.Spectrum();
    result.setId(input.getID());
    result.setFormat(input.getFormat());

    // spectral data
    waves = new nz.ac.waikato.adams.webservice.spectral.get.Waves();
    for (SpectrumPoint point: input) {
      wave = new nz.ac.waikato.adams.webservice.spectral.get.Wave();
      wave.setNumber(point.getWaveNumber());
      wave.setAmplitude(point.getAmplitude());
      waves.getWave().add(wave);
    }
    result.setWaves(waves);
    
    // report
    props = new nz.ac.waikato.adams.webservice.spectral.get.Properties();
    if (input.hasReport()) {
      report = input.getReport();
      for (AbstractField field: report.getFields()) {
	prop = new nz.ac.waikato.adams.webservice.spectral.get.Property();
	prop.setKey(field.getName());
	prop.setType(nz.ac.waikato.adams.webservice.spectral.get.DataType.valueOf(field.getDataType().toRaw()));
	prop.setValue("" + report.getValue(field));
	props.getProp().add(prop);
      }
    }
    
    result.setProps(props);
    
    return result;
  }

  /**
   * Converts a Webservice spectrum into a KNIR one.
   * 
   * @param input	the KNIR spectrum
   * @return		the Webservice spectrum
   */
  public static adams.data.spectrum.Spectrum webserviceToKnir(nz.ac.waikato.adams.webservice.spectral.get.Spectrum input) {
    adams.data.spectrum.Spectrum	result;
    adams.data.spectrum.SpectrumPoint	point;
    adams.data.sampledata.SampleData	report;
    Field 				field;
    
    result = new adams.data.spectrum.Spectrum();
    
    // spectral data
    for (nz.ac.waikato.adams.webservice.spectral.get.Wave wave: input.getWaves().getWave()) {
      point = new SpectrumPoint(wave.getNumber(), wave.getAmplitude());
      result.add(point);
    }
    
    // report
    report = new adams.data.sampledata.SampleData();
    for (nz.ac.waikato.adams.webservice.spectral.get.Property prop: input.getProps().getProp()) {
      field = new Field(prop.getKey(), adams.data.report.DataType.valueOf(prop.getType().toString()));
      report.addField(field);
      report.setValue(
	  field, 
	  prop.getValue());
    }
    
    result.setReport(report);
    
    result.setID(input.getId());
    result.setFormat(input.getFormat());
    
    return result;
  }
}
