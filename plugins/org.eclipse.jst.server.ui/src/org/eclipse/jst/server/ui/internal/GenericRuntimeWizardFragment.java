/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.internal;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
/**
 * 
 */
public class GenericRuntimeWizardFragment extends WizardFragment {
	protected GenericRuntimeComposite comp;
	
	/**
	 * Create a new fragment.
	 */
	public GenericRuntimeWizardFragment() {
		// do nothing
	}

	/**
	 * @see WizardFragment#hasComposite()
	 */
	public boolean hasComposite() {
		return true;
	}

	/**
	 * @see WizardFragment#createComposite(Composite, IWizardHandle)
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new GenericRuntimeComposite(parent, wizard);
		return comp;
	}

	/**
	 * @see WizardFragment#isComplete()
	 */
	public boolean isComplete() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
		
		if (runtime == null)
			return false;
		IStatus status = runtime.validate(null);
		return (status != null && status.isOK());
	}

	/**
	 * @see WizardFragment#enter()
	 */
	public void enter() {
		if (comp != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
			comp.setRuntime(runtime);
		}
	}
}
