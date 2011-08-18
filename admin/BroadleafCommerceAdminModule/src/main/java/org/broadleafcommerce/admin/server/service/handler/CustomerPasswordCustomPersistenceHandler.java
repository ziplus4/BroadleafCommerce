/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.server.service.handler;

import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.util.PasswordReset;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.hibernate.tool.hbm2x.StringUtils;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
public class CustomerPasswordCustomPersistenceHandler implements CustomPersistenceHandler {
	
	@Resource(name="blCustomerService")
	protected CustomerService customerService;

	public Boolean canHandleFetch(PersistencePackage persistencePackage) {
		return false;
	}

	public Boolean canHandleAdd(PersistencePackage persistencePackage) {
		return false;
	}

	public Boolean canHandleRemove(PersistencePackage persistencePackage) {
		return false;
	}

	public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
		String[] customCriteria = persistencePackage.getCustomCriteria();
		return customCriteria != null && customCriteria.length > 0 && customCriteria[0].equals("passwordUpdate");
	}

	public Boolean canHandleInspect(PersistencePackage persistencePackage) {
		return false;
	}

	public DynamicResultSet inspect(PersistencePackage persistencePackage, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		throw new RuntimeException("custom inspect not supported");
	}

	public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom fetch not supported");
	}

	public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom add not supported");
	}

	public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom remove not supported");
	}

	public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
		Customer customer = customerService.readCustomerByUsername(entity.findProperty("username").getValue());
		if (StringUtils.isEmpty(customer.getEmailAddress())) {
			throw new ServiceException("Unable to update password because an email address is not available for this customer. An email address is required to send the customer the new system generated password.");
		}
		
		PasswordReset passwordReset = new PasswordReset();
		passwordReset.setUsername(entity.findProperty("username").getValue());
		passwordReset.setPasswordChangeRequired(false);
		passwordReset.setEmail(customer.getEmailAddress());
		passwordReset.setPasswordLength(22);
		passwordReset.setSendResetEmailReliableAsync(false);
		
		customer = customerService.resetPassword(passwordReset);
		
		return entity;
	}
	
	
	
}
