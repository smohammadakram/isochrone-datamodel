package datamodel.command;

import java.io.IOException;

public interface ICommand {
	void execute() throws IOException;
}
