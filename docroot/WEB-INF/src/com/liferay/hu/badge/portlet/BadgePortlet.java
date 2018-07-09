package com.liferay.hu.badge.portlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;

import org.apache.log4j.Logger;

import com.liferay.hu.badge.service.service.BadgeServiceUtil;
import com.liferay.hu.badge.service.service.SubscriberServiceUtil;
import com.liferay.hu.badge.utils.Emails;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;

public class BadgePortlet extends MVCPortlet {
	private boolean isAdminMode(ActionRequest request) {
		PortletPreferences pp = request.getPreferences();

		boolean prefAdmin = GetterUtil.getBoolean(pp.getValue("adminmode", "false"));

		return prefAdmin;
	}

	public void addBadgeAction(ActionRequest request, ActionResponse actionResponse)
			throws IOException, PortletException {
		PortalUtil.getCompanyId(request);

		long toUserId = GetterUtil.getLong(request.getParameter("toUser"), -1);
		_log.debug("toUserId:" + toUserId);
		if (toUserId == -1) {
			_log.debug("toUserId was not sent in request");
			return;
		}

		String description = GetterUtil.getString(request.getParameter("description"), StringPool.BLANK);

		User user = (User) request.getAttribute(WebKeys.USER);

		if ((user == null ) || (user.getUserId() <= 0)) {
			_log.debug("logged in user is null or userId <= 0");
			return;
		}

		PortletPreferences pp = request.getPreferences();

		int badgeType = getBadgeType(pp);

		boolean isSelfAdminMode = GetterUtil.getBoolean(pp.getValue("selfadminmode", "false"));

		long fromUserId = user.getUserId();

		Calendar date = new GregorianCalendar();

		if (isAdminMode(request)) {
			long tmpUserId = GetterUtil.getLong(request.getParameter("fromUser"), -1);
			if (tmpUserId >= 0) {
				fromUserId = tmpUserId;
			}
		}

		if (isSelfAdminMode || isAdminMode(request)) {
			int year = GetterUtil.getInteger(request.getParameter("assignYear"), -1);
			int month = GetterUtil.getInteger(request.getParameter("assignMonth"), -1);
			int day = GetterUtil.getInteger(request.getParameter("assignDay"), -1);

			if ((year > 0) && (month > 0) && (day > 0)) {
				date.set(year, month, day);
			}
		}

		BadgeServiceUtil.addBadge(date, fromUserId, toUserId, badgeType, description);
		Emails.notifyUser(toUserId, fromUserId, badgeType, description, request);
		Emails.notifySubscribers(
			SubscriberServiceUtil.getSubscribers(), fromUserId, toUserId,
			badgeType, description, request);

		sendRedirect(request, actionResponse);
	}

	public void editPreferencesAction(ActionRequest request, ActionResponse response)
			throws IOException, PortletException {

		PortletPreferences pp = request.getPreferences();

		for (String param: editCheckboxParameters) {
			boolean paramValue = GetterUtil.getBoolean(request.getParameter(param), false);
			pp.setValue(param, Boolean.toString(paramValue));
		}

		for (String param: editRadioParameters) {
			boolean paramValue = param.equals(request.getParameter("restrictbadgetype"));
			pp.setValue(param, Boolean.toString(paramValue));
		}

		pp.store();

		response.setPortletMode(PortletMode.VIEW);
	}

	/**
	 * Default is ThankYou badge, so when nobody set portlet preferences, portlet
	 * will add and show thank you badges.
	 * 
	 * This method should be changed when there will be more badgetypes!
	 * 
	 * @param ppref
	 * @return
	 */
	public static int getBadgeType(PortletPreferences ppref) {
		int badgeType = BADGETYPE_THANKYOU;

		boolean addthankyou = GetterUtil.getBoolean(ppref.getValue("addthankyou", "true"), true);

		if (!addthankyou) {
			badgeType = BADGETYPE_RESPECT;
		}

		return badgeType;
	}

	/**
	 * Default is ThankYou badge, so when nobody set portlet preferences, portlet
	 * will add and show thank you badges.
	 * 
	 * This method should be changed when there will be more badgetypes!
	 * 
	 * @param ppref
	 * @return
	 */
	public static String getBadgeTypeString(PortletPreferences ppref) {
		int badgeType = getBadgeType(ppref);

		return BADGETYPE_STRINGS[badgeType];
	}

	/**
	 * Default is ThankYou badge, so when nobody set portlet preferences, portlet
	 * will add and show thank you badges.
	 * 
	 * This method should be changed when there will be more badgetypes!
	 * 
	 * @param long badgeTypef
	 * @return
	 */
	public static String getBadgeTypeString(long badgeType) {
		if (badgeType < 0 || badgeType >= BADGETYPE_STRINGS.length) {
			badgeType = 0;
		}

		return BADGETYPE_STRINGS[(int)badgeType];
	}

	private Logger _log = Logger.getLogger(getClass());

	private static String[] editCheckboxParameters =
		{"showthankyou", "showrespect", 
		"displayform", "displaybadges", "adminmode", "selfadminmode"};

	private static String[] editRadioParameters =
		{"addthankyou", "addrespect"};

	public static int BADGETYPE_THANKYOU = 0;
	public static int BADGETYPE_RESPECT = 1;
	public static String[] BADGETYPE_STRINGS = {"thankyou", "respect"};

}
