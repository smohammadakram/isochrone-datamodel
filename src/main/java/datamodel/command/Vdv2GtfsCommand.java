package datamodel.command;

import java.io.File;

import org.onebusaway.vdv452.Vdv452ToGtfsConverter;

public class Vdv2GtfsCommand implements ICommand {
	private String inputPath;
	private String outputPath;

	// Constructor

	public Vdv2GtfsCommand(final String inputPath, final String outputPath) {
		super();
		this.inputPath = inputPath;
		this.outputPath = outputPath;
	}

	// Public methods

	@Override
	public void execute() {
		final Vdv452ToGtfsConverter converter = new Vdv452ToGtfsConverter();
		converter.setInputPath(new File(inputPath));
		converter.setOutputPath(new File(outputPath));

		CommandUtils.uncheck(converter::run);
	}

}
