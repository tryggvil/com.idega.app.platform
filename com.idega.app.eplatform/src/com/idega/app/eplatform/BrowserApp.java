/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package com.idega.app.eplatform;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.idega.app.eplatform.appservermanager.WebappInstance;
import com.idega.app.eplatform.appservermanager.AppserverManager;
import com.idega.app.eplatform.appservermanager.WebappStartedListener;
import com.idega.app.eplatform.appservermanager.AppservermanagerPlugin;

/**
 * The application class for the RCP Browser Example. Creates and runs the
 * Workbench, passing a <code>BrowserAdvisor</code> as the workbench advisor.
 * 
 * @issue Couldn't run without initial perspective -- it failed with NPE on
 *        WorkbenchWindow.openPage (called from Workbench.openFirstTimeWindow).
 *        Advisor is currently required to override
 *        getInitialWindowPerspectiveId.
 * 
 * @issue If shortcut bar is hidden, and last view in perspective is closed,
 *        there's no way to get it open again.
 * 
 * @since 3.0
 */
public class BrowserApp implements IApplication {

	
	public final static boolean TABBED_UI=false;
	public final static boolean HISTORY_ENABLED=false;
	public final static boolean LOCATIONBAR_ENABLED=false;
	/**
	 * Constructs a new <code>BrowserApp</code>.
	 */
	public BrowserApp() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			displayStartupMessage(display);
			int code = PlatformUI.createAndRunWorkbench(display, new BrowserAdvisor());
			// exit the application with an appropriate return code
			return code == PlatformUI.RETURN_RESTART ? EXIT_RESTART : EXIT_OK;
			// paintSplash(display);
		}
		finally {
			if (display != null)
				display.dispose();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}

	private AppserverManager getManager() {
		return AppservermanagerPlugin.getPlugin().getManager();
	}
	
	

	protected boolean isStarted() {
		return getManager().getMainWebapp().isStarted();
	}

	public void displayStartupMessage(final Display display) {
		final StartupMessageWindow window = new StartupMessageWindow(display);
		window.show();
		window.setMessageText(getManager().getMainWebapp().getStatus());
		final Thread statusHandlerThread = new Thread() {

			public void run() {
				while (!isStarted()) {
					display.asyncExec(new Runnable() {
						public void run() {
							window.setMessageText(getManager().getMainWebapp().getStatus());
						}
					});
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		statusHandlerThread.start();
		
		
		WebappStartedListener startedListener = new WebappStartedListener(){
			public void notifyStarted(WebappInstance instance) {
				display.wake();
			}
		};
		getManager().getMainWebapp().setStartedListener(startedListener);
		
		 // Set up the event loop.
		while (!isStarted()) {
			if (!display.readAndDispatch()) {
				 // If no more entries in event queue
				display.sleep();
			}
		}
		window.dispose();
		
	}
	

}
