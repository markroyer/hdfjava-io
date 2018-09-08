package edu.umaine.cs.simple;

import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * A simple application that creates an octave h5 file.
 * 
 * @author Mark Royer
 *
 */
public class Application implements IApplication {

	@Override
	public Object start(final IApplicationContext context) throws Exception {

		final Map<?, ?> args = context.getArguments();
		final String[] appArgs = (String[]) args.get("application.args");
		for (final String arg : appArgs) {
			System.out.println(arg);
		}
		
		System.out.println("Start application!");
		
		new H5FileExample().run(appArgs);
		
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// This is only called to interrupt the application
	}
}
