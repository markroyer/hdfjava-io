package edu.umaine.cs.simple;

import java.io.File;
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

		System.out.println("Start application!");

		final Map<?, ?> args = context.getArguments();
		final String[] appArgs = (String[]) args.get("application.args");
		for (final String arg : appArgs) {
			System.out.println(arg);
		}

		H5FileExample example = new H5FileExample(new File("test.h5"));

		example.writeFile();

		System.out.printf("File successfully written to %s.\n",
				example.getFile().getAbsolutePath());

		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// This is only called to interrupt the application
	}
}
