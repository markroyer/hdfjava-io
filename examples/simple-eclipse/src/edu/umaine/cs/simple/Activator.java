package edu.umaine.cs.simple;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Start and stop the application.
 * 
 * @author Mark Royer
 *
 */
public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Start plug-in");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stop plug-in");
	}

}
