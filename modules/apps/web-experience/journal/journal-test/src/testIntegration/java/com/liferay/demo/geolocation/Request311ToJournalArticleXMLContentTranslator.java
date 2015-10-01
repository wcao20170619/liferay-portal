package com.liferay.demo.geolocation;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.xml.SAXReaderImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Request311ToJournalArticleXMLContentTranslator {

	public static void main(String[] args) throws Exception {
		Parser_311_requests_json json = new Parser_311_requests_json();

		GeolocationDemoDataset dataset = json.parse();

		System.out.println(
			new Request311ToJournalArticleXMLContentTranslator()
				.translate(dataset)
				);

		System.out.println(
			"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"
			+ "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
	}

	Collection<String> translate(GeolocationDemoDataset dataset) throws Exception {
		final ArrayList<String> xmls = new ArrayList<>(dataset.size());

		dataset.accept(new GeolocationDemoDataset.Visitor() {

			@Override
			public void visit(Request311 entry) {
				xmls.add(translate(entry));
			}

		});

		return xmls;
	}

	protected String translate(Request311 r) {
		Document document = createEmptyDocument();

		String geolocation =
				"{ "
				+ "latitude: " + r.latitude
				+ ", "
				+ "longitude: " + r.longitude
				+ " }";

		String name =
				r.case_title + " " +
				geolocation + " " +
				r.location + " | " +
				r.closure_reason;

		addFieldSampleStructuredContent(
			document, "name", name);
		addFieldSampleStructuredContent(
			document, "geolocation", geolocation);

		addFieldSampleStructuredContent(
			document, "case_enquiry_id", r.case_enquiry_id);
		addFieldSampleStructuredContent(
			document, "case_title", r.case_title);
		addFieldSampleStructuredContent(
			document, "city_council_district", r.city_council_district);
		addFieldSampleStructuredContent(
			document, "closed_dt", r.closed_dt);
		addFieldSampleStructuredContent(
			document, "closure_reason", r.closure_reason);
		addFieldSampleStructuredContent(
			document, "department", r.department);
		addFieldSampleStructuredContent(
			document, "fire_district", r.fire_district);
		addFieldSampleStructuredContent(
			document, "location", r.location);
		addFieldSampleStructuredContent(
			document, "location_street_name", r.location_street_name);
		addFieldSampleStructuredContent(
			document, "neighborhood", r.neighborhood);
		addFieldSampleStructuredContent(
			document, "ontime_status", r.ontime_status);
		addFieldSampleStructuredContent(
			document, "open_dt", r.open_dt);
		addFieldSampleStructuredContent(
			document, "police_district", r.police_district);
		addFieldSampleStructuredContent(
			document, "property_id", r.property_id);
		addFieldSampleStructuredContent(
			document, "pwd_district", r.pwd_district);
		addFieldSampleStructuredContent(
			document, "queue", r.queue);
		addFieldSampleStructuredContent(
			document, "reason", r.reason);
		addFieldSampleStructuredContent(
			document, "subject", r.subject);
		addFieldSampleStructuredContent(
			document, "target_dt", r.target_dt);
		addFieldSampleStructuredContent(
			document, "type", r.type);

		return document.asXML();
	}

	protected Document createEmptyDocument() {

		Map<Locale, String> contents = new HashMap<>();

		contents.put(Locale.US, "title");

		StringBundler availableLocales =
			getAvaiableLocales(Collections.singletonList(contents));

		Document document =
			createDocumentContent(availableLocales.toString(), "en_US");
		return document;
	}

	protected StringBundler getAvaiableLocales(
		List<Map<Locale, String>> contents) {

		StringBundler availableLocales = new StringBundler(2 * contents.size());

		for (Map<Locale, String> map : contents) {
			StringBundler sb = new StringBundler(2 * map.size());

			for (Locale locale : map.keySet()) {
				sb.append(LocaleUtil.toLanguageId(locale));
				sb.append(StringPool.COMMA);
			}

			sb.setIndex(sb.index() - 1);

			availableLocales.append(sb);
		}
		return availableLocales;
	}

	protected Document createDocumentContent(
		String availableLocales, String defaultLocale) {

		Document document = new SAXReaderImpl().createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addAttribute("available-locales", availableLocales);
		rootElement.addAttribute("default-locale", defaultLocale);
		rootElement.addElement("request");

		return document;
	}

	protected String addFieldSampleStructuredContent(
		Document document, String name, String value) {

		Map<Locale, String> contents = new HashMap<>();
		contents.put(Locale.US, value);

		return addFieldSampleStructuredContent(
			document, name, Collections.singletonList(contents));
	}

	protected String addFieldSampleStructuredContent(
		Document document,
		String name, List<Map<Locale, String>> contents) {

		Element rootElement = document.getRootElement();

		for (Map<Locale, String> map : contents) {
			Element dynamicElementElement = rootElement.addElement(
				"dynamic-element");

			dynamicElementElement.addAttribute("index-type", "keyword");
			dynamicElementElement.addAttribute("name", name);
			dynamicElementElement.addAttribute("type", "text");

			for (Map.Entry<Locale, String> entry : map.entrySet()) {
				Element element = dynamicElementElement.addElement(
					"dynamic-content");

				element.addAttribute(
					"language-id", LocaleUtil.toLanguageId(entry.getKey()));
				element.addCDATA(entry.getValue());
			}
		}

		return document.asXML();
	}

}
