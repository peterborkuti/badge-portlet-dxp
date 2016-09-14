/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the applicable 
 * Liferay software end user license agreement ("License Agreement")
 * found on www.liferay.com/legal/eulas. You may also contact Liferay, Inc.
 * for a copy of the License Agreement. You may not use this file except in
 * compliance with the License Agreement. 
 * See the License Agreement for the specific language governing
 * permissions and limitations under the License Agreement, including 
 * but not limited to distribution rights of the Software.
 *
 */

package com.liferay.hu.badge.service.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.hu.badge.service.model.Badge;
import com.liferay.hu.badge.service.service.BadgeLocalServiceUtil;

/**
 * The extended model base implementation for the Badge service. Represents a row in the &quot;BadgePortlet_Badge&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This class exists only as a container for the default extended model level methods generated by ServiceBuilder. Helper methods and all application logic should be put in {@link BadgeImpl}.
 * </p>
 *
 * @author Borkuti Peter
 * @see BadgeImpl
 * @see Badge
 * @generated
 */
@ProviderType
public abstract class BadgeBaseImpl extends BadgeModelImpl implements Badge {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a badge model instance should use the {@link Badge} interface instead.
	 */
	@Override
	public void persist() {
		if (this.isNew()) {
			BadgeLocalServiceUtil.addBadge(this);
		}
		else {
			BadgeLocalServiceUtil.updateBadge(this);
		}
	}
}