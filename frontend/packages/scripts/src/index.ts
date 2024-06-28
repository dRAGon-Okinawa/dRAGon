import cac from 'cac';
import { blue, lightGreen } from 'kolorist';
import { version } from '../package.json';
import { cleanup, generateRoute, updatePkg } from './commands';
import { loadCliOptions } from './config';

type Command = 'cleanup' | 'update-pkg' | 'gen-route';

type CommandAction<A extends object> = (args?: A) => Promise<void> | void;

type CommandWithAction<A extends object = object> = Record<Command, { desc: string; action: CommandAction<A> }>;

interface CommandArg {
  /** Execute additional command after bumping and before git commit. Defaults to 'pnpm sa changelog' */
  execute?: string;
  /** Indicates whether to push the git commit and tag. Defaults to true */
  push?: boolean;
  /** Generate changelog by total tags */
  total?: boolean;
  /**
   * The glob pattern of dirs to cleanup
   *
   * If not set, it will use the default value
   *
   * Multiple values use "," to separate them
   */
  cleanupDir?: string;
}

export async function setupCli() {
  const cliOptions = await loadCliOptions();

  const cli = cac(blue('dragon-frontend'));

  cli
    .version(lightGreen(version))
    .help();

  const commands: CommandWithAction<CommandArg> = {
    cleanup: {
      desc: 'delete dirs: node_modules, dist, etc.',
      action: async () => {
        await cleanup(cliOptions.cleanupDirs);
      }
    },
    'update-pkg': {
      desc: 'update package.json dependencies versions',
      action: async () => {
        await updatePkg(cliOptions.ncuCommandArgs);
      }
    },
    'gen-route': {
      desc: 'generate route',
      action: async () => {
        await generateRoute();
      }
    }
  };

  for (const [command, { desc, action }] of Object.entries(commands)) {
    cli.command(command, lightGreen(desc)).action(action);
  }

  cli.parse();
}

setupCli();
