/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.model.InternalInitializer;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class RuntimeWorkingCopy extends Runtime implements IRuntimeWorkingCopy {
	protected String PROP_ID_SET = "id-set";
	protected Runtime runtime;
	protected WorkingCopyHelper wch;
	
	protected RuntimeDelegate workingCopyDelegate;

	/**
	 * Create a new runtime working copy from existing runtime.
	 * 
	 * @param runtime
	 */
	public RuntimeWorkingCopy(Runtime runtime) {
		super(runtime.getFile());
		this.runtime = runtime;
		
		runtimeType = runtime.getRuntimeType();
		
		map = new HashMap(runtime.map);
		wch = new WorkingCopyHelper(this);
	}
	
	/**
	 * Create a new runtime working copy for a new runtime.
	 * 
	 * @param file
	 * @param id
	 * @param runtimeType
	 */
	public RuntimeWorkingCopy(IFile file, String id, IRuntimeType runtimeType) {
		super(file, id, runtimeType);
		wch = new WorkingCopyHelper(this);
		wch.setDirty(true);
		
		if (id == null || id.length() == 0) {
			id = ServerPlugin.generateId();
			map.put(PROP_ID, id);
		} else
			setAttribute(PROP_ID_SET, true);
		
		// throw CoreException if the id already exists
	}

	/**
	 * @see IRuntime#isWorkingCopy()
	 */
	public boolean isWorkingCopy() {
		return true;
	}

	/**
	 * @see IRuntime#createWorkingCopy()
	 */
	public IRuntimeWorkingCopy createWorkingCopy() {
		return this;
	}

	public void setAttribute(String attributeName, int value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, boolean value) {
		wch.setAttribute(attributeName, value);
	}
	
	public void setAttribute(String attributeName, String value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, List value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, Map value) {
		wch.setAttribute(attributeName, value);
	}

	/**
	 * @see IRuntimeWorkingCopy#setName(String)
	 */
	public void setName(String name) {
		wch.setName(name);
		boolean set = getAttribute(PROP_ID_SET, false);
		if (runtime == null && !set)
			setAttribute(PROP_ID, name);
	}

	public void setTestEnvironment(boolean b) {
		setAttribute(PROP_TEST_ENVIRONMENT, b);
	}
	
	/**
	 * @see IRuntimeWorkingCopy#setStub(boolean)
	 */
	public void setStub(boolean b) {
		setAttribute(PROP_STUB, b);
	}

	/**
	 * @see IRuntimeWorkingCopy#isDirty()
	 */
	public boolean isDirty() {
		return wch.isDirty();
	}
	
	/**
	 * @see IRuntimeWorkingCopy#getOriginal()
	 */
	public IRuntime getOriginal() {
		return runtime;
	}

	/**
	 * @see IRuntimeWorkingCopy#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean b) {
		wch.setLocked(b);
	}

	public void setPrivate(boolean b) {
		wch.setPrivate(b);
	}

	/**
	 * @see IRuntimeWorkingCopy#setLocation(IPath)
	 */
	public void setLocation(IPath path) {
		if (path == null)
			setAttribute(PROP_LOCATION, (String)null);
		else
			setAttribute(PROP_LOCATION, path.toString());
	}

	/**
	 * @see IRuntimeWorkingCopy#save(boolean, IProgressMonitor)
	 */
	public IRuntime save(boolean force, IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.subTask(NLS.bind(Messages.savingTask, getName()));
		
		if (!force && getOriginal() != null)
			wch.validateTimestamp(((Runtime) getOriginal()).getTimestamp());
		
		int timestamp = getTimestamp();
		map.put(PROP_TIMESTAMP, Integer.toString(timestamp+1));
		
		IRuntime origRuntime = runtime;
		if (runtime == null)
			runtime = new Runtime(file);
		
		String oldId = getId();
		String name = getName();
		boolean set = getAttribute(PROP_ID_SET, false);
		if (!oldId.equals(name) && !set) {
			setAttribute(PROP_ID, name);
		} else
			oldId = null;
		
		runtime.setInternal(this);
		runtime.saveToMetadata(monitor);
		wch.setDirty(false);
		
		if (oldId != null)
			updateRuntimeReferences(oldId, name, origRuntime);
		
		return runtime;
	}

	protected void updateRuntimeReferences(final String oldId, final String newId, final IRuntime origRuntime) {
		// TODO fix me
		/*class UpdateRuntimeReferencesJob extends Job {
			public UpdateRuntimeReferencesJob() {
				super(NLS.bind(Messages.savingTask, newId));
			}

			public IStatus run(IProgressMonitor monitor) {
				// fix .runtime files
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				if (projects != null) {
					int size = projects.length;
					for (int i = 0; i < size; i++) {
						ProjectProperties props = (ProjectProperties) ServerCore.getProjectProperties(projects[i]);
						if (oldId.equals(props.getRuntimeTargetId())) {
							try {
								props.setRuntimeTargetId(newId, monitor);
							} catch (Exception e) {
								Trace.trace(Trace.SEVERE, "Error setting runtime target", e);
							}
						}
					}
				}
				
				// save servers
				if (runtime != null) {
					ResourceManager rm = ResourceManager.getInstance();
					IServer[] servers = rm.getServers();
					if (servers != null) {
						int size = servers.length;
						for (int i = 0; i < size; i++) {
							if (oldId.equals(((Server)servers[i]).getRuntimeId())) {
								try {
									ServerWorkingCopy wc = (ServerWorkingCopy) servers[i].createWorkingCopy();
									wc.setRuntimeId(newId);
									wc.save(false, monitor);
								} catch (Exception e) {
									// ignore
								}
							}
						}
					}
				}
				
				return new Status(IStatus.OK, ServerPlugin.PLUGIN_ID, 0, "", null);
			}
		}
		UpdateRuntimeReferencesJob job = new UpdateRuntimeReferencesJob();
		job.schedule();*/
	}

	protected RuntimeDelegate getWorkingCopyDelegate(IProgressMonitor monitor) {
		if (workingCopyDelegate != null)
			return workingCopyDelegate;
		
		synchronized (this) {
			if (workingCopyDelegate == null) {
				try {
					long time = System.currentTimeMillis();
					workingCopyDelegate = ((RuntimeType) runtimeType).createRuntimeDelegate();
					InternalInitializer.initializeRuntimeDelegate(workingCopyDelegate, this, monitor);
					Trace.trace(Trace.PERFORMANCE, "RuntimeWorkingCopy.getWorkingCopyDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getRuntimeType().getId());
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), e);
				}
			}
		}
		return workingCopyDelegate;
	}

	public void dispose() {
		super.dispose();
		if (workingCopyDelegate != null)
			workingCopyDelegate.dispose();
	}

	/**
	 * Add a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		wch.addPropertyChangeListener(listener);
	}
	
	/**
	 * Remove a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		wch.removePropertyChangeListener(listener);
	}
	
	/**
	 * Fire a property change event.
	 * 
	 * @param propertyName a property name
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		wch.firePropertyChangeEvent(propertyName, oldValue, newValue);
	}
	
	/**
	 * Set the defaults.
	 * 
	 * @param monitor
	 */
	protected void setDefaults(IProgressMonitor monitor) {
		try {
			getWorkingCopyDelegate(monitor).setDefaults(monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate setDefaults() " + toString(), e);
		}
	}
}