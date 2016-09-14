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
 * UpdateSampleDataPanel.java
 * Copyright (C) 2016 FracPete (fracpete at gmail dot com)
 *
 */

package adams.gui.tools;

import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.sampledata.SampleData;
import adams.db.AbstractSpectrumConditions;
import adams.db.DatabaseConnection;
import adams.db.SampleDataT;
import adams.db.SpectrumConditionsMulti;
import adams.gui.core.BaseObjectTextField;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseTable;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.selection.SelectSpectrumPanel;
import adams.gui.visualization.spectrum.SampleDataFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows the user to update/set values in selected spectra.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UpdateSampleDataPanel
  extends BasePanel {

  /**
   * Table model for displaying the database IDs, IDs, formats and selected
   * state of spectra.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class TableModel
    extends SelectSpectrumPanel.TableModel {

    /** for serialization. */
    private static final long serialVersionUID = 2776199413402687115L;

    /** whether a spectrum got selected. */
    protected boolean[] m_Selected;

    /**
     * default constructor.
     */
    public TableModel() {
      this(new String[0]);
    }

    /**
     * the constructor.
     *
     * @param values	the IDs/Names/Instruments to display
     */
    public TableModel(List<String> values) {
      this(values.toArray(new String[values.size()]));
    }

    /**
     * the constructor.
     *
     * @param values	the IDs/Names/Instruments to display
     */
    public TableModel(String[] values) {
      super(values);
      m_Selected = new boolean[values.length];
    }

    /**
     * Returns the number of columns in the table, i.e., 3.
     *
     * @return		the number of columns, always 3
     */
    public int getColumnCount() {
      return 4;
    }

    /**
     * Returns the name of the column.
     *
     * @param column 	the column to get the name for
     * @return		the name of the column
     */
    public String getColumnName(int column) {
      if (column == 0)
	return "Update";
      else if (column == 1)
	return "Database ID";
      else if (column == 2)
	return "Sample ID";
      else if (column == 3)
	return "Format";
      else
	throw new IllegalArgumentException("Column " + column + " is invalid!");
    }

    /**
     * Returns the class type of the column.
     *
     * @param columnIndex	the column to get the class for
     * @return			the class for the column
     */
    public Class getColumnClass(int columnIndex) {
      if (columnIndex == 0)
	return Boolean.class;
      else if (columnIndex == 1)
	return Integer.class;
      else if (columnIndex == 2)
	return String.class;
      else if (columnIndex == 3)
	return String.class;
      else
	throw new IllegalArgumentException("Column " + columnIndex + " is invalid!");
    }

    /**
     * Returns the ID at the given position.
     *
     * @param row	the row
     * @param column	the column
     * @return		the ID
     */
    public Object getValueAt(int row, int column) {
      if (column == 0)
	return m_Selected[row];
      else if (column == 1)
	return m_IDs[row];
      else if (column == 2)
	return m_SampleID[row];
      else if (column == 3)
	return m_Format[row];
      else
	throw new IllegalArgumentException("Column " + column + " is invalid!");
    }

    /**
     * Returns whether the cell is editable.
     *
     * @param rowIndex		the row
     * @param columnIndex	the column
     * @return			true if editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return (columnIndex == 0);
    }

    /**
     * Sets the value of the cell.
     *
     * @param aValue		the value to set
     * @param rowIndex		the row
     * @param columnIndex	the column
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      if (columnIndex != 0)
	return;
      m_Selected[rowIndex] = (Boolean) aValue;
    }

    /**
     * Returns whether the spectrum at the specified position is selected.
     *
     * @param row	the (actual, not visible) position of the spectrum
     * @return		true if selected
     */
    public boolean getSelectedAt(int row) {
      return ((row >= 0) && (row < m_Selected.length)) && m_Selected[row];
    }

    /**
     * Marks all spectra as selected.
     */
    public void selectAll() {
      select(true);
    }

    /**
     * Marks all spectra as un-selected.
     */
    public void selectNone() {
      select(false);
    }

    /**
     * Marks all spectra with the specified select state.
     */
    protected void select(boolean select) {
      for (int i = 0; i < m_Selected.length; i++)
	m_Selected[i] = select;

      fireTableDataChanged();
    }

    /**
     * Inverts the selection state.
     */
    public void invertSelection() {
      for (int i = 0; i < m_Selected.length; i++)
	m_Selected[i] = !m_Selected[i];

      fireTableDataChanged();
    }

    /**
     * Returns how many spectra are currently selected.
     *
     * @return		the number of selected spectra
     */
    public int getSelectedCount() {
      int	result;

      result = 0;

      for (boolean sel: m_Selected)
        result += (sel) ? 1 : 0;

      return result;
    }

    /**
     * Returns the selected items (sample IDs).
     *
     * @return		the selected items
     */
    public String[] getSelectedItems() {
      List<String>	result;
      int		i;

      result = new ArrayList<>();

      for (i = 0; i < getRowCount(); i++) {
	if (getSelectedAt(i))
	  result.add(m_SampleID[i]);
      }

      return result.toArray(new String[result.size()]);
    }

    /**
     * Clears the internal model.
     */
    public void clear() {
      super.clear();
      m_Selected = new boolean[0];

      fireTableDataChanged();
    }
  }

  /** the from date. */
  protected BaseObjectTextField<BaseDate> m_TextFrom;

  /** the to date. */
  protected BaseObjectTextField<BaseDate> m_TextTo;

  /** the button for the options. */
  protected JButton m_ButtonConditions;

  /** the button for the search. */
  protected JButton m_ButtonSearch;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the table model in use. */
  protected TableModel m_Model;

  /** the table with the spectra. */
  protected SortableAndSearchableTableWithButtons m_TableIDs;

  /** the button for selecting all. */
  protected JButton m_ButtonSelectAll;

  /** the button for selecting none. */
  protected JButton m_ButtonSelectNone;

  /** the button for inverting the selection. */
  protected JButton m_ButtonSelectInvert;

  /** the sample data table. */
  protected SampleDataFactory.Table m_TableSampleData;

  /** the text field for the field name. */
  protected JTextField m_TextName;

  /** the combobox for the field data type. */
  protected JComboBox<DataType> m_ComboBoxType;

  /** the text field for the field value. */
  protected JTextField m_TextValue;

  /** the button for updating the sample data. */
  protected JButton m_ButtonApply;

  /** the button for closing the dialog. */
  protected JButton m_ButtonClose;

  /** the conditions to use in the search. */
  protected SpectrumConditionsMulti m_Conditions;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** whether the search is currently happening. */
  protected boolean m_Searching;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Conditions = new SpectrumConditionsMulti();
    m_Searching  = false;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    JPanel	panelAll;
    JPanel	panel;
    JPanel	panel2;
    JLabel	label;
    BaseDate	bdate;

    super.initGUI();

    setLayout(new BorderLayout());

    panelAll = new JPanel(new BorderLayout());
    add(panelAll, BorderLayout.CENTER);

    // 1. search
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    panelAll.add(panel, BorderLayout.NORTH);

    bdate = new BaseDate(BaseDate.NOW);

    // from
    m_TextFrom = new BaseObjectTextField<>(new BaseDate(bdate.dateValue()));
    m_TextFrom.setColumns(10);
    label = new JLabel("From");
    label.setDisplayedMnemonic('F');
    label.setLabelFor(m_TextFrom);
    panel.add(label);
    panel.add(m_TextFrom);

    // to
    m_TextTo = new BaseObjectTextField<>(new BaseDate(bdate.dateValue()));
    m_TextTo.setColumns(10);
    label = new JLabel("To");
    label.setDisplayedMnemonic('T');
    label.setLabelFor(m_TextTo);
    panel.add(label);
    panel.add(m_TextTo);

    // options
    m_ButtonConditions = new JButton("Options");
    m_ButtonConditions.setMnemonic('O');
    m_ButtonConditions.addActionListener((ActionEvent e) -> showConditions());
    panel.add(m_ButtonConditions);

    // search
    m_ButtonSearch = new JButton("Search");
    m_ButtonSearch.setMnemonic('S');
    m_ButtonSearch.addActionListener((ActionEvent e) -> search());
    panel.add(m_ButtonSearch);

    // 2. table and report
    panel = new JPanel(new BorderLayout());
    panelAll.add(panel, BorderLayout.CENTER);

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    panel.add(m_SplitPane, BorderLayout.CENTER);

    m_Model = new TableModel();
    m_TableIDs = new SortableAndSearchableTableWithButtons(m_Model);
    m_TableIDs.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_ButtonSelectAll = new JButton("All");
    m_ButtonSelectAll.addActionListener((ActionEvent e) -> m_Model.selectAll());
    m_TableIDs.addToButtonsPanel(m_ButtonSelectAll);
    m_ButtonSelectNone = new JButton("None");
    m_ButtonSelectNone.addActionListener((ActionEvent e) -> m_Model.selectNone());
    m_TableIDs.addToButtonsPanel(m_ButtonSelectNone);
    m_ButtonSelectInvert = new JButton("Invert");
    m_ButtonSelectInvert.addActionListener((ActionEvent e) -> m_Model.invertSelection());
    m_TableIDs.addToButtonsPanel(m_ButtonSelectInvert);
    m_SplitPane.setLeftComponent(m_TableIDs);

    m_TableSampleData = new SampleDataFactory.Table();
    m_SplitPane.setRightComponent(m_TableSampleData);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    panel.add(panel2, BorderLayout.SOUTH);

    m_TextName = new JTextField(10);
    m_TextName.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	updateButtons();
      }
    });
    label = new JLabel("Field");
    label.setDisplayedMnemonic('d');
    label.setLabelFor(m_TextName);
    panel2.add(label);
    panel2.add(m_TextName);

    m_ComboBoxType = new JComboBox<>(DataType.values());
    m_ComboBoxType.setSelectedItem(DataType.BOOLEAN);
    panel2.add(m_ComboBoxType);

    m_TextValue = new JTextField(10);
    m_TextValue.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateButtons();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
	updateButtons();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
	updateButtons();
      }
    });
    panel2.add(m_TextValue);

    // 3. apply
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panel, BorderLayout.SOUTH);

    m_ButtonApply = new JButton("Apply");
    m_ButtonApply.setMnemonic('A');
    m_ButtonApply.addActionListener((ActionEvent e) -> apply());
    panel.add(m_ButtonApply);

    m_ButtonClose = new JButton("Close");
    m_ButtonClose.setMnemonic('l');
    m_ButtonClose.addActionListener((ActionEvent e) -> closeParent());
    panel.add(m_ButtonClose);

    // 4. status bar
    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Transfers the fields to the conditions object.
   */
  protected void fieldsToConditions() {
    m_Conditions.setStartDate(new BaseDateTime(m_TextFrom.getObject().getValue() + " 00:00:00"));
    m_Conditions.setEndDate(new BaseDateTime(m_TextTo.getObject().getValue() + " 23:59:59"));
  }

  /**
   * Transfers the conditions to the fields.
   */
  protected void conditionsToFields() {
    m_TextFrom.setObject(new BaseDate(m_Conditions.getStartDate().dateValue()));
    m_TextTo.setObject(new BaseDate(m_Conditions.getEndDate().dateValue()));
  }

  /**
   * Shows GOE dialog with the conditions.
   */
  protected void showConditions() {
    GenericObjectEditorDialog	dialog;

    fieldsToConditions();

    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), Dialog.ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setTitle("Sample data conditions");
    dialog.getGOEEditor().setCanChangeClassInDialog(false);
    dialog.getGOEEditor().setClassType(AbstractSpectrumConditions.class);
    dialog.setCurrent(m_Conditions);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    m_Conditions = (SpectrumConditionsMulti) dialog.getCurrent();
    conditionsToFields();
  }

  /**
   * Performs the search and updates the table.
   */
  protected void search() {
    SwingWorker		worker;

    worker = new SwingWorker() {
      protected List<String> ids;
      @Override
      protected Object doInBackground() throws Exception {
	MouseUtils.setWaitCursor(UpdateSampleDataPanel.this);
	m_Searching = true;
	updateButtons();
	SampleDataT sdt = SampleDataT.getSingleton(DatabaseConnection.getSingleton());
	ids = sdt.getIDs(new String[]{"sp.AUTO_ID", "sp.SAMPLEID", "sp.FORMAT"}, m_Conditions);
	return null;
      }
      @Override
      protected void done() {
	super.done();
	m_Model = new TableModel(ids);
	m_TableIDs.setModel(m_Model);
	MouseUtils.setDefaultCursor(UpdateSampleDataPanel.this);
	m_Searching = false;
	updateButtons();
	if (ids.size() == 0) {
	  GUIHelper.showErrorMessage(
	    UpdateSampleDataPanel.this, "Failed to retrieve any IDs from database, check console for potential errors!");
	}
      }
    };
    worker.execute();
  }

  /**
   * Updates the selected spectra.
   */
  protected void apply() {
    final Field		field;
    final String	value;
    final String[]	sel;
    SwingWorker		worker;

    field = new Field(m_TextName.getText(), (DataType) m_ComboBoxType.getSelectedItem());
    value = m_TextValue.getText();
    sel   = m_Model.getSelectedItems();

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	MouseUtils.setWaitCursor(UpdateSampleDataPanel.this);
	SampleDataT sdt = SampleDataT.getSingleton(DatabaseConnection.getSingleton());
	for (int i = 0; i < sel.length; i++) {
	  m_StatusBar.showStatus("Updating: " + (i+1) + "/" + sel.length + "...");
	  SampleData sd = sdt.load(sel[i]);
	  if (sd != null) {
	    sd.setValue(field, value);
	    if (!sdt.store(sel[i], sd))
	      GUIHelper.showErrorMessage(
		UpdateSampleDataPanel.this, "Failed to store sample data for ID " + sel[i] + "!");
	  }
	}
	return null;
      }
      @Override
      protected void done() {
	super.done();
	MouseUtils.setDefaultCursor(UpdateSampleDataPanel.this);
	m_StatusBar.clearStatus();
      }
    };
    worker.execute();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    int		selCount;

    selCount = m_Model.getSelectedCount();

    m_ButtonApply.setEnabled(!m_Searching && (selCount > 0) && !m_TextName.getText().isEmpty() && m_TextValue.getText().isEmpty());
    m_ButtonConditions.setEnabled(!m_Searching);
    m_ButtonSearch.setEnabled(!m_Searching);
  }
}
