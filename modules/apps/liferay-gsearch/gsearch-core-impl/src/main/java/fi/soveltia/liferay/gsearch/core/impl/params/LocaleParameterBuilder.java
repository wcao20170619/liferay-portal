
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * Locale parameter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class LocaleParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(QueryContext queryContext)
		throws Exception {

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		queryContext.setParameter(
			ParameterNames.LOCALE, themeDisplay.getLocale());
	}

	@Override
	public void addParameter(
		QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {

		if (parameters.containsKey(ParameterNames.LOCALE)) {
			queryContext.setParameter(
				ParameterNames.LOCALE,
				(Locale) parameters.get(ParameterNames.LOCALE));
		}
		else {
			
			// Set at least the default 

			if (parameters.containsKey(ParameterNames.COMPANY_ID)) {

				long companyId =
					(long) parameters.get(ParameterNames.COMPANY_ID);

				long guestGroupId = _groupLocalService.getGroup(companyId, GroupConstants.GUEST).getGroupId();
				
				queryContext.setParameter(
					ParameterNames.LOCALE,
					_portal.getSiteDefaultLocale(guestGroupId));
			}
		}
	}

	@Override
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException {

		return true;
	}

	@Override
	public boolean validate(
		QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException {

		return true;
	}

	@Reference
	Portal _portal;

	@Reference
	GroupLocalService _groupLocalService;

}
