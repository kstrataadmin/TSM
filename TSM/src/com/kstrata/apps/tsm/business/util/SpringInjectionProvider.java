package com.kstrata.apps.tsm.business.util;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.sun.faces.spi.InjectionProvider;
import com.sun.faces.spi.InjectionProviderException;
import com.sun.faces.vendor.WebContainerInjectionProvider;

public class SpringInjectionProvider implements InjectionProvider {

	private static final WebContainerInjectionProvider CONTAINER_INJECTION_PROVIDER = new WebContainerInjectionProvider();

	@Override
	public void inject(Object managedBean) throws InjectionProviderException {
		CONTAINER_INJECTION_PROVIDER.inject(managedBean);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void invokePostConstruct(Object managedBean) throws InjectionProviderException {
		ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		if (null != webApplicationContext) {
			try{
				webApplicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(managedBean, AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT, false);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		CONTAINER_INJECTION_PROVIDER.invokePostConstruct(managedBean);
	}

	@Override
	public void invokePreDestroy(Object managedBean) throws InjectionProviderException {
		CONTAINER_INJECTION_PROVIDER.invokePreDestroy(managedBean);
	}
}
