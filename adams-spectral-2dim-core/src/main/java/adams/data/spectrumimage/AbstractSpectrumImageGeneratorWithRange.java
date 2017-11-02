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
 * AbstractSpectrumImageGeneratorWithRange.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.spectrumimage;

/**
 * Ancestor for spectrum image generators that limit the amplitude ranges.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSpectrumImageGeneratorWithRange
  extends AbstractSpectrumImageGenerator {

  private static final long serialVersionUID = -2796398928812431488L;

  /** the minimum amplitude to use. */
  protected float m_MinAmplitude;

  /** the maximum amplitude to use. */
  protected float m_MaxAmplitude;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-amplitude", "minAmplitude",
      0.0f);

    m_OptionManager.add(
      "max-amplitude", "maxAmplitude",
      1000.0f);
  }

  /**
   * Sets the minimum amplitude to assume.
   *
   * @param value	the minimum
   */
  public void setMinAmplitude(float value) {
    if (getOptionManager().isValid("minAmplitude", value)) {
      m_MinAmplitude = value;
      reset();
    }
  }

  /**
   * Returns the minimum amplitude to assume.
   *
   * @return		the minimum
   */
  public float getMinAmplitude() {
    return m_MinAmplitude;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or
   *         for listing the options.
   */
  public String minAmplitudeTipText() {
    return "The minimum amplitude to assume; amplitudes below get set to this value.";
  }

  /**
   * Sets the maximum amplitude to assume.
   *
   * @param value	the maximum
   */
  public void setMaxAmplitude(float value) {
    if (getOptionManager().isValid("maxAmplitude", value)) {
      m_MaxAmplitude = value;
      reset();
    }
  }

  /**
   * Returns the maximum amplitude to assume.
   *
   * @return		the maximum
   */
  public float getMaxAmplitude() {
    return m_MaxAmplitude;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or
   *         for listing the options.
   */
  public String maxAmplitudeTipText() {
    return "The maximum amplitude to assume; amplitudes below get set to this value.";
  }
}
