package net.eikehirsch.clewarecontrol.process

/**
 * Default implementation of the {@link ProcessStarter ProcessStarterInterface}. This will create 'real' processes.
 */
class ProcessBuilderProcessStarter implements ProcessStarter {

  @Override
  Process start(String[] cmd) {
	return new ProcessBuilder(cmd).redirectErrorStream(true).start()
  }
}
