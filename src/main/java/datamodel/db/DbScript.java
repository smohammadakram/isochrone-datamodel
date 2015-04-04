package datamodel.db;

import java.util.Collection;
import java.util.LinkedList;


public class DbScript {
	private final boolean batch;
	private final String name;
	private final String script;

	public DbScript(final String name, final String script, final boolean batch) {
		this.batch = batch;
		this.name = name;
		this.script = script;
	}

	public DbScript(final String name, final String script) {
		this(name, script, false);
	}

	public DbScript(final String script, final boolean batch) {
		this(null, script, batch);
	}

	public String[] getCommands() {
		final String[] strArr = script.split("(\r)?\n");
		final Collection<String> result = new LinkedList<String>();

		int i = 0;
		final StringBuilder sb = new StringBuilder();
		for (final String str : strArr) {
			final String s = str.trim() + "\n";
			if (s.startsWith("--")) {
				continue;
			}

			sb.append(s);
			if (str.endsWith(";")) {
				result.add(sb.toString());
				sb.setLength(0);
				++i;
			}
		}

		return result.toArray(new String[i]);
	}

	public String getName() {
		return name;
	}

	public boolean isBatch() {
		return batch;
	}

}
