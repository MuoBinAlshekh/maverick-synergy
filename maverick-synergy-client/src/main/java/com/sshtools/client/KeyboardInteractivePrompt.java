package com.sshtools.client;

/**
 *  Represents a keyboard-interactive prompt.
 */
public class KeyboardInteractivePrompt {
	private String prompt;
	private String response;
	private boolean echo;

	/**
	 * Creates a new KBIPrompt object.
	 *
	 * @param prompt
	 * @param echo
	 */
	public KeyboardInteractivePrompt(String prompt, boolean echo) {
		this.prompt = prompt;
		this.echo = echo;
	}

	/**
	 * Get the prompt message to display to the user
	 *
	 * @return String
	 */
	public String getPrompt() {
		return prompt;
	}

	/**
	 * <tt>true</tt> if the user response should be echo'd to the display,
	 * otherwise <tt>false</tt>.
	 *
	 * @return boolean
	 */
	public boolean echo() {
		return echo;
	}

	/**
	 * Set the user's response for this prompt.
	 *
	 * @param response
	 */
	public void setResponse(String response) {
		this.response = response;
	}

	/**
	 * Get the user's response for this prompt.
	 *
	 * @return String
	 */
	public String getResponse() {
		return response;
	}
}
