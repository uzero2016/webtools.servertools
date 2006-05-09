/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.eclipse.jst.server.generic.internal.core.util.ExtensionPointUtil;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerLaunchConfigurationTab;


public class GenericLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup{
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup ILaunchConfigurationTabGroup#createTabs(ILaunchConfigurationDialog, String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[7];
		tabs[0] = new ServerLaunchConfigurationTab(getTypeIds());
		tabs[0].setLaunchConfigurationDialog(dialog);
		tabs[1] = new JavaArgumentsTab();
		tabs[1].setLaunchConfigurationDialog(dialog);
		tabs[2] = new JavaClasspathTab();
		tabs[2].setLaunchConfigurationDialog(dialog);
		tabs[3] = new SourceLookupTab();
		tabs[3].setLaunchConfigurationDialog(dialog);
		tabs[4] = new EnvironmentTab();
		tabs[4].setLaunchConfigurationDialog(dialog);
		tabs[5] = new JavaJRETab();
		tabs[5].setLaunchConfigurationDialog(dialog);	 
		tabs[6] = new CommonTab();
		tabs[6].setLaunchConfigurationDialog(dialog);
		setTabs(tabs);
	}

	private String[] getTypeIds() {
		ArrayList list = new ArrayList();
		IExtension[] extensions= ExtensionPointUtil.getGenericServerDefinitionExtensions();
		 for (int i = 0; extensions!=null && i < extensions.length; i++) {
			 IExtension extension = extensions[i];
	         IConfigurationElement[] elements = ExtensionPointUtil.getConfigurationElements(extension);
	         for (int j = 0; j < elements.length; j++) {
	        	 IConfigurationElement element = elements[j];
	        	 String genericRuntimeID = element.getAttribute("id");
	        	 IServerType[] serverTypes = ServerCore.getServerTypes();
	        	 for (int k = 0; k < serverTypes.length; k++) {
					if(serverTypes[k].hasRuntime() && serverTypes[k].getRuntimeType().getId().equals(genericRuntimeID))
						list.add(serverTypes[k].getId());
				}
	         }
		 }
		 return (String[])list.toArray(new String[list.size()]);
	}
	

}