/**
 * (c) 2002-2021 JADAPTIVE Limited. All Rights Reserved.
 *
 * This file is part of the Maverick Synergy Java SSH API.
 *
 * Maverick Synergy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Maverick Synergy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Maverick Synergy.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.sshtools.server.vsession;

import java.io.IOException;
import java.util.List;

import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import com.sshtools.common.permissions.PermissionDeniedException;

public interface Command {

	public static final int STILL_ACTIVE = Integer.MIN_VALUE;
	
	public abstract void run(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException;

	public abstract String getDescription();

	public abstract String getSubsystem();

	public abstract String getCommandName();

	public abstract String getUsage();

	public abstract boolean isBuiltIn();

	public abstract int getExitCode();

	public abstract boolean isHidden();
	
	public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates);


}