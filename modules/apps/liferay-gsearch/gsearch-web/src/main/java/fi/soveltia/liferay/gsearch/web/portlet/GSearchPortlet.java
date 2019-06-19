
package fi.soveltia.liferay.gsearch.web.portlet;

import com.liferay.portal.portlet.bridge.soy.SoyPortletRegister;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;

/**
 * GSearch portlet class.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.application-type=widget",
		"com.liferay.portlet.css-class-wrapper=gsearch-portlet",
		"com.liferay.portlet.display-category=category.gsearch",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-css=/js/bootstrap-datepicker/css/bootstrap-datepicker.css",
		"com.liferay.portlet.layout-cacheable=false",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"com.liferay.portlet.scopeable=false",
		"com.liferay.portlet.single-page-application=false",
		"javax.portlet.display-name=gsearch-portlet",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.copy-request-parameters=false",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=GSearch",
		"javax.portlet.name=" + GSearchPortletKeys.GSEARCH_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	}
)
public class GSearchPortlet implements SoyPortletRegister {

}