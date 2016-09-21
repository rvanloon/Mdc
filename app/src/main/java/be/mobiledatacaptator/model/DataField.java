package be.mobiledatacaptator.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.utilities.MdcUtil;

public class DataField extends TableRow implements TextWatcher,
		OnItemSelectedListener {

	private String name;
	private String label;
	private Tab tab;
	private Boolean required = false;
	private final double nullValue = -9999;
	private double min = nullValue;
	private double max = nullValue;
	private VeldType type;
	private String defaultValue;
	private String link;
	private List<ChoiceItem> choiceItems = new ArrayList<ChoiceItem>();
	private boolean executeLinkOnExit = false;

	private Boolean activateLink = false; // Deze wordt pas true na het inlezen
											// van de
	// template en bestaande data. Dit om te
	// voorkomen dat bestaande data aangepast
	// wordt.

	private TextView textViewLabel;
	private EditText editTextValue;
	private Spinner spinnerChoice;

	private Element xmlTemplate;

	public DataField(Context context, Element xml, Tab tab) {
		super(context);
		xmlTemplate = xml;
		this.tab = tab;
		loadTemplate();
		activateLink = true;
	}

	public DataField(Context context) {
		super(context);
	}

	public void appendXml(Document doc, Element root) {

		if (executeLinkOnExit)
			executeLink();

		Element element = doc.createElement(name);
		root.appendChild(element);

		if (type == VeldType.CHOICE) {
			if (spinnerChoice.getSelectedItem() != null) {
				element.appendChild(doc.createTextNode(spinnerChoice
						.getSelectedItem().toString()));
				if (((ChoiceItem) spinnerChoice.getSelectedItem()).getId() > -9999)
					element.setAttribute(
							getContext().getString(R.string.IdnForXmlAttr),
							String.valueOf(((ChoiceItem) spinnerChoice
									.getSelectedItem()).getId()));
			}
		} else {
			if (editTextValue.getText() != null) {
				element.appendChild(doc.createTextNode(editTextValue.getText()
						.toString()));
			}
		}
	}

	@SuppressLint("DefaultLocale")
	private void loadTemplate() {
		// Velden invullen aan hand van xml element.
		if (xmlTemplate.hasAttribute("Name"))
			name = xmlTemplate.getAttribute("Name");
		if (xmlTemplate.hasAttribute("Label"))
			label = xmlTemplate.getAttribute("Label");
		if (xmlTemplate.hasAttribute("DefaultValue"))
			defaultValue = xmlTemplate.getAttribute("DefaultValue");
		if (xmlTemplate.hasAttribute("Link"))
			link = xmlTemplate.getAttribute("Link");
		if (xmlTemplate.hasAttribute("Required"))
			if (xmlTemplate.getAttribute("Required")
					.toLowerCase(Locale.getDefault()).equals("true"))
				required = true;
		if (xmlTemplate.hasAttribute("Min"))
			min = Double.parseDouble(xmlTemplate.getAttribute("Min"));
		if (xmlTemplate.hasAttribute("Max"))
			max = Double.parseDouble(xmlTemplate.getAttribute("Max"));
		if (xmlTemplate.hasAttribute("Type")) {
			String strType = xmlTemplate.getAttribute("Type").toLowerCase(
					Locale.getDefault());
			if (strType.equals("text"))
				type = VeldType.TEXT;
			if (strType.equals("choice"))
				type = VeldType.CHOICE;
			if (strType.equals("int"))
				type = VeldType.INT;
			if (strType.equals("double"))
				type = VeldType.DOUBLE;
		}
		NodeList temp = xmlTemplate.getElementsByTagName("Choices");
		if (temp.getLength() > 0) {
			NodeList keuzes = ((Element) temp.item(0))
					.getElementsByTagName("Choice");
			// Blanco Item toevoegen
			choiceItems.add(new ChoiceItem(-9999, ""));
			for (int l = 0; l < keuzes.getLength(); l++) {
				Element keuzeNode = (Element) keuzes.item(l);
				choiceItems.add(new ChoiceItem(Integer.parseInt(keuzeNode
						.getAttribute("idn")), keuzeNode.getAttribute("Text")));
			}
		}

		// Label plaatsen
		textViewLabel = new TextView(getContext());
		textViewLabel.setText(label + ": ");
		textViewLabel.setTextAppearance(getContext(),
				android.R.style.TextAppearance_DeviceDefault_Medium);
		addView(textViewLabel);

		if (type == VeldType.CHOICE) {

			// Idien keuzelijst, spinner plaatsen
			spinnerChoice = new Spinner(getContext());
			ArrayAdapter<ChoiceItem> adapter = new ArrayAdapter<ChoiceItem>(
					getContext(), android.R.layout.simple_spinner_item,
					choiceItems);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerChoice.setAdapter(adapter);
			spinnerChoice.setOnItemSelectedListener(this);
			spinnerChoice.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.WRAP_CONTENT, 1f));
			addView(spinnerChoice);

		} else {

			// Anders textveld plaatsen
			editTextValue = new EditText(getContext());
			editTextValue.setSingleLine();
			editTextValue.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.WRAP_CONTENT, 1f));
			editTextValue.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			if (type == VeldType.DOUBLE)
				editTextValue.setKeyListener(new DigitsKeyListener(true, true));
			if (type == VeldType.INT)
				editTextValue
						.setKeyListener(new DigitsKeyListener(true, false));

			editTextValue.addTextChangedListener(this);
			addView(editTextValue);
		}

		// Defaultvalue inlezen
		String s = null;
		if (defaultValue != null)
			s = defaultValue;
		setValue(s);

		// Locken indien nodig
		if (xmlTemplate.hasAttribute("Locked")) {
			if (xmlTemplate.getAttribute("Locked")
					.toLowerCase(Locale.getDefault()).equals("true")) {
				if (type == VeldType.CHOICE) {
					spinnerChoice.setEnabled(false);
				} else {
					editTextValue.setEnabled(false);
				}
			}
		}

		// Afhankelijkheden instellen
		if (!(link == null || link.equals(""))) {
			String[] linkArr = link.split(";");
			if (linkArr.length == 3 && linkArr[0].equals("GET")) {
				if (linkArr[2].equals("START"))
					executeLink();
				if (linkArr[2].equals("END"))
					executeLinkOnExit = true;
			}
		}
	}

	public void setValue(String value) {
		activateLink = false;
		if (type == VeldType.CHOICE) {
			for (int i = 0; i < choiceItems.size(); i++) {
				if (choiceItems.get(i).getText().equals(value)) {
					boolean enabled = spinnerChoice.isEnabled();
					spinnerChoice.setEnabled(true);
					spinnerChoice.setSelection(i);
					spinnerChoice.setEnabled(enabled);
					break;
				}
			}
		} else {
			boolean enabled = editTextValue.isEnabled();
			editTextValue.setEnabled(true);
			editTextValue.setText(value);
			editTextValue.setEnabled(enabled);
		}
		activateLink = true;
		executeLinkOnExit = false;
	}

	private String getValue() {
		if (type == VeldType.CHOICE) {
			if (spinnerChoice.getSelectedItem() != null) {
				return spinnerChoice.getSelectedItem().toString();
			}
		} else {
			if (editTextValue.getText() != null) {
				return editTextValue.getText().toString();
			}
		}
		return null;
	}

	public Boolean isValide(StringBuilder errMsg) {
		String value = getValue();

		// Required
		if (required && (value == null || value.equals(""))) {
			errMsg.append(getContext().getString(R.string.ValVerplicht));
			return false;
		}

		if (!(value == null || value.equals(""))) {
			Double d = 0.0;
			try {
				d = Double.parseDouble(value);
			} catch (NumberFormatException e) {
			}

			// Min
			if (min != nullValue && (d < min)) {
				errMsg.append(getContext().getString(R.string.ValTeLaag));
				return false;
			}

			// Max
			if (max != nullValue && (d > max)) {
				errMsg.append(getContext().getString(R.string.ValTeHoog));
				return false;
			}
		}
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	private void executeLink() {
		try {
			if (!(link == null || link.equals(""))) {
				String[] linkArr = link.split(";");
				String toFieldName;
				for (int i = 0; i < linkArr.length; i++) {
					if (linkArr[i].equals("FIELD")) {
						i++;
						toFieldName = linkArr[i];
						i++;
						if (linkArr[i].equals("DELETE")) {
							getDatafieldByName(toFieldName).setValue(null);
						} else if (linkArr[i].equals("CALCULATE")) {
							BigDecimal d1, d2;
							i++;
							try {
								if (linkArr[i].equals("THIS")) {
									d1 = new BigDecimal(getValue());
								} else {
									DataField df = getFirstNonEmtyDatafieldByName(linkArr[i]);
									d1 = new BigDecimal(
											df != null ? df.getValue()
													: linkArr[i]);
								}
							} catch (NumberFormatException e) {
								d1 = new BigDecimal(0);
							}
							i++;
							while (!(linkArr[i].equals("END"))) {
								try {
									if (linkArr[i].equals("THIS")) {
										d2 = new BigDecimal(getValue());
									} else {
										DataField df = getFirstNonEmtyDatafieldByName(linkArr[i]);
										d2 = new BigDecimal(
												df != null ? df.getValue()
														: linkArr[i]);
									}
								} catch (NumberFormatException e) {
									d2 = new BigDecimal(0);
								}
								i++;
								if (linkArr[i].equals("+")) {
									d1 = d1.add(d2);
								} else if (linkArr[i].equals("-")) {
									d1 = d1.subtract(d2);
								} else if (linkArr[i].equals("*")) {
									d1 = d1.multiply(d2);
								} else if (linkArr[i].equals("/")) {
									d1 = d1.divide(d2);
								} else {
									throw new Error();
								}
								i++;
							}
							getDatafieldByName(toFieldName).setValue(
									d1.toString());
						}
					} else if (linkArr[i].equals("TABTITLE")) { // Vorm:
																// "TABTITLE;prefix;suffix"
						String tName = linkArr[++i] + getValue() + linkArr[++i];
						List<Tab> tabs = tab.getGroup().getTabs();
						Boolean uniek = true;
						for (Tab t : tabs) {
							if (t.getName().equals(tName)
									&& !tab.getName().equals(tName)) {
								uniek = false;
							}
						}
						if (uniek) {
							tab.setName(tName);
							tab.getGroup().notifyDataSetChanged();
						} else {
							MdcUtil.showToastShort(R.string.TabExist,
									this.getContext());
							setValue("");
						}
					} else if (linkArr[i].equals("GET")) {
						i++;
						if (linkArr[i].equals("getFICHENAME")) {
							setValue(UnitOfWork
									.getInstance()
									.getActiveFiche()
									.getName()
									.substring(
											UnitOfWork.getInstance()
													.getActiveProject()
													.getFilePrefix().length()));
						} else if (linkArr[i].equals("getDATE")) {
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"yyyy/MM/dd");
							Date date = new Date();
							setValue(dateFormat.format(date));
						} else if (linkArr[i].equals("getTIME")) {
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"HH:mm:ss");
							Date date = new Date();
							setValue(dateFormat.format(date));
						} else {
							DataField df = getDatafieldByName(linkArr[i]);
							setValue(df == null ? linkArr[i] : df.getValue());
						}

						i++;
					}
				}
			}
		} catch (Exception e) {
			MdcUtil.showToastLong(getContext().getString(R.string.LinkFout),
					getContext());
		}
	}

	private DataField getDatafieldByName(String name) {
		String[] adress = name.split("\\.");
		if (adress.length == 1) {
			for (DataField dataField : tab.getDataFields()) {
				if (dataField.getName().equals(adress[0]))
					return dataField;
			}
		} else if (adress.length == 2) {
			for (Tab t : tab.getGroup().getTabs()) {
				if (t.getName().equals(adress[0])) {
					for (DataField dataField : t.getDataFields()) {
						if (dataField.getName().equals(adress[1]))
							return dataField;
					}
				}
			}
		} else {
			for (Group g : tab.getGroup().getFiche().getGroups()) {
				if (g.getName().equals(adress[0])) {
					for (Tab t : g.getTabs()) {
						if (t.getName().equals(adress[1])) {
							for (DataField dataField : t.getDataFields()) {
								if (dataField.getName().equals(adress[2]))
									return dataField;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private DataField getFirstNonEmtyDatafieldByName(String name) {
		String fne = "FIRSTNONEMPTY";
		if (name.toUpperCase().startsWith("FIRSTNONEMPTY")) {
			String[] fields = name.substring(fne.length() + 1,
					name.length() - 1).split(",");
			for (String s : fields) {
				DataField dataField = getDatafieldByName(s);
				if (dataField.getValue() != null
						&& !dataField.getValue().isEmpty())
					return dataField;
			}
			return null;
		} else
			return getDatafieldByName(name);
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// Validatie op textveld
		editTextValue.setError(null);
		StringBuilder errMsg = new StringBuilder();
		if (!(isValide(errMsg)))
			editTextValue.setError(errMsg.toString());
		if (activateLink)
			executeLink();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (activateLink)
			executeLink();
	}

	// -----------------------------------------------------------------------------------------------------
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

}
