package net.eikehirsch.clewarecontrol.process

/**
 * Used to start any given command with an ProcessBuilder or to create a mock for tests ;)
 */
interface ProcessStarter {

  /**
   * This will start the actual command and return the created {@link Process}.
   *
   * @param cmd An array containing the command and any given option.
   *
   * @return The created Process
   */
  Process start(String[] cmd)

}