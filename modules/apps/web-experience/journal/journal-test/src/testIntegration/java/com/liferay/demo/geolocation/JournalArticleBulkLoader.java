package com.liferay.demo.geolocation;

import static com.liferay.dynamic.data.mapping.model.DDMFormFieldType.TEXT;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.service.test.ServiceTestUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class JournalArticleBulkLoader {

	public JournalArticleBulkLoader(
		GeolocationDemoDataset dataset, int limit, boolean dryRun) {

		this.dataset = dataset;
		this.limit = limit;
		this.dryRun = dryRun;
	}

	public void load() throws Exception {
		init();

		dataset.accept(new GeolocationDemoDataset.Visitor() {

			@Override
			public void visit(Request311 entry) throws Exception {
				addArticle(entry);
			}

		});

		System.out.println(
				"***********************************************\n"+
				" Geolocation demo dataset loaded successfully. \n"+
				"***********************************************\n"
			);
	}

	private void addArticle(Request311 entry) throws Exception {
		if (count >= limit) return;
		count++;

		String title = entry.case_title;
		String xml = translator.translate(entry);

		System.out.println("" + count + "/" + limit);
		System.out.println(title);
		System.out.println(xml);

		if (dryRun) return;

		addArticle(title, xml);
	}

	private void addArticle(String title, String xml) throws Exception {
		ServiceContext serviceContext1 =
			ServiceContextTestUtil.getServiceContext(
				_serviceContext.getScopeGroupId());

		Map<Locale, String> titleMap = new HashMap<>();

		titleMap.put(LocaleUtil.getSiteDefault(), title);

		JournalArticleLocalServiceUtil.addArticle(
			serviceContext1.getUserId(), serviceContext1.getScopeGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			0, StringPool.BLANK, true, 0, titleMap,
			null, xml,
			ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
			null, 1, 1, 1965, 0, 0,
			0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, true, false, null, null,
			null, null, serviceContext1);
	}

	private void addFieldToForm(
		DDMForm ddmForm, String name, String dataType, String type,
		String indexType) {

		DDMFormField ddmFormField = new DDMFormField(name, type);

		ddmFormField.setDataType(dataType);
		ddmFormField.setIndexType(indexType);
		ddmFormField.setLocalizable(true);
		ddmFormField.setRepeatable(false);

		LocalizedValue label = new LocalizedValue(defaultLocale);

		label.addString(
			defaultLocale, "Field_" + LocaleUtil.toLanguageId(defaultLocale));

		for (Locale locale : _availableLocalesSet) {
			label.addString(locale, "Field_" + LocaleUtil.toLanguageId(locale));
		}

		ddmFormField.setLabel(label);

		ddmForm.addDDMFormField(ddmFormField);
	}

	private void createDDMFormStructureAndTemplate() throws Exception {
		DDMForm ddmForm = getDDMForm();

		ddmStructure = getDDMStructure(_serviceContext, ddmForm);

		ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_serviceContext.getScopeGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));
	}

	private DDMForm getDDMForm() {
		String keyword_indexType = "keyword";
		String text_indexType = "text";

		boolean repeatable = false;

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			"name", "string", text_indexType, repeatable, TEXT,
			new Locale[] {LocaleUtil.US}, LocaleUtil.US);

		addFieldToForm(ddmForm, "geolocation", "string", DDMFormFieldType.GEOLOCATION, keyword_indexType);
		addFieldToForm(ddmForm, "case_enquiry_id", "string", TEXT, keyword_indexType); // 101000295623
		addFieldToForm(ddmForm, "case_title", "string", TEXT, text_indexType); // Improper Storage of Trash (Barrels)
		addFieldToForm(ddmForm, "city_council_district", "string", TEXT, keyword_indexType); // 1
		addFieldToForm(ddmForm, "closed_dt", "datetime", "datetime", keyword_indexType); // 2011-07-06T16:55:32
		addFieldToForm(ddmForm, "closure_reason", "string", TEXT, text_indexType); // Case Closed VIOISS: Violation Filed
		addFieldToForm(ddmForm, "department", "string", TEXT, keyword_indexType); // ISD
		addFieldToForm(ddmForm, "fire_district", "string", TEXT, keyword_indexType); // 1
		addFieldToForm(ddmForm, "location", "string", TEXT, text_indexType); // 347 Meridian St  East Boston  MA  02128
		addFieldToForm(ddmForm, "location_street_name", "string", TEXT, text_indexType); // 347 Meridian St
		addFieldToForm(ddmForm, "neighborhood", "string", TEXT, text_indexType); // East Boston
		addFieldToForm(ddmForm, "ontime_status", "string", TEXT, keyword_indexType); // ONTIME
		addFieldToForm(ddmForm, "open_dt", "datetime", "datetime", keyword_indexType); // 2011-07-05T14:48:09
		addFieldToForm(ddmForm, "police_district", "string", TEXT, keyword_indexType); // a7
		addFieldToForm(ddmForm, "property_id", "string", TEXT, keyword_indexType); // 84847
		addFieldToForm(ddmForm, "pwd_district", "string", TEXT, keyword_indexType); // 09
		addFieldToForm(ddmForm, "queue", "string", TEXT, text_indexType); // ISD_Code Enforcement (INTERNAL)
		addFieldToForm(ddmForm, "reason", "string", TEXT, text_indexType); // Code Enforcement
		addFieldToForm(ddmForm, "subject", "string", TEXT, text_indexType); // Inspectional Services
		addFieldToForm(ddmForm, "target_dt", "datetime", "datetime", keyword_indexType); // 2011-07-07T14:46:15
		addFieldToForm(ddmForm, "type", "string", TEXT, text_indexType); // Improper Storage of Trash (Barrels)

		return ddmForm;
	}

	private DDMStructure getDDMStructure(
		ServiceContext serviceContext, DDMForm ddmForm)
			throws Exception {

		DDMStructure structure = DDMStructureTestUtil.addStructure(
			serviceContext.getScopeGroupId(), JournalArticle.class.getName(),
			ddmForm);

		return structure;
	}

	private void init()
		throws Exception {

		ServiceTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		createDDMFormStructureAndTemplate();
	}

	private final static Locale defaultLocale = LocaleUtil.US;

	protected BaseModel<?> _baseModel;
	protected Group _group;

	private final Locale[] _availableLocales = new Locale[] {
		LocaleUtil.US
	};

	private final Set<Locale> _availableLocalesSet =
		SetUtil.fromArray(_availableLocales);

	private ServiceContext _serviceContext;
	private int count;
	private final GeolocationDemoDataset dataset;
	private DDMStructure ddmStructure;
	private DDMTemplate ddmTemplate;
	private final boolean dryRun;
	private final int limit;
	private final Request311ToJournalArticleXMLContentTranslator translator = new Request311ToJournalArticleXMLContentTranslator();
}
