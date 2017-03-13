package edu.umaine.cs.simple;

import java.io.File;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import edu.umaine.cs.h5.octave.H5OctaveWriter;

/**
 * A simple application that creates an octave h5 file.
 * 
 * @author Mark Royer
 *
 */
public class Application implements IApplication {

	@Override
	public Object start(final IApplicationContext context) throws Exception {

		System.out.println("Start application");

		final Map<?, ?> args = context.getArguments();
		final String[] appArgs = (String[]) args.get("application.args");
		for (final String arg : appArgs) {
			System.out.println(arg);
		}

		H5OctaveWriter out = new H5OctaveWriter();

		final double[][] double2D = { { 0, 1, 2, 3, 4, 5 },
				{ 0.5, 1.5, 2.5, 3.5, 4.5, 5.5 } };

		final String str = "Strawberry Banana";

		final int[][][] int3D = { { { 1, 2 }, { 3, 4 } },
				{ { 5, 6 }, { 7, 8 } }, { { 9, 10 }, { 11, 12 } } };

		final char[] charArray = { 'a', 'b', 'c' };

		final File file = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + "test.h5");

		final String[] labels = { "double2D", "str", "int3D", "charArray" };

		final Object[] objects = { double2D, str, int3D, charArray };

		out.writeHDF5File(file.getAbsolutePath(), labels, objects);

		System.out.printf("File successfully written to %s\n",
				file.getAbsolutePath());

		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// This is only called to interrupt the application
	}
}
