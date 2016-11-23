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
 * GotoWaveNumber.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.spectrum.plotpopup;

import adams.data.spectrum.Spectrum;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.plotpopup.AbstractPlotPopupCustomizer;
import adams.gui.visualization.spectrum.SpectrumContainer;
import adams.gui.visualization.spectrum.SpectrumContainerManager;
import adams.gui.visualization.spectrum.SpectrumPanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Displays the selected wave number.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GotoWaveNumber
  extends AbstractPlotPopupCustomizer<Spectrum, SpectrumContainerManager, SpectrumContainer> {

  private static final long serialVersionUID = 3295471324320509106L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Goto wave number";
  }

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "graphics";
  }

  /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
  @Override
  public boolean handles(DataContainerPanelWithContainerList<Spectrum, SpectrumContainerManager, SpectrumContainer> panel) {
    return (panel instanceof SpectrumPanel);
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param panel	the affected panel
   * @param e		the mouse event
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(DataContainerPanelWithContainerList<Spectrum, SpectrumContainerManager, SpectrumContainer> panel, MouseEvent e, JPopupMenu menu) {
    JMenuItem		item;

    item = new JMenuItem("Goto wave number...", GUIHelper.getEmptyIcon());
    item.addActionListener((ActionEvent ae) -> ((SpectrumPanel) panel).selectWaveNumber());
    menu.add(item);
  }
}
