package ai.dragon.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.boot.CommandLineRunner;

import ai.dragon.enumeration.CommandLineExecutionResultType;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

public abstract class CommandLineRunnerWithArgumentsParser implements CommandLineRunner {
    public abstract CommandLineExecutionResultType runCommand(String... args) throws Exception;

    private String commandName;
    private ArgumentParser parser;

    public void run(String... args) throws Exception {
        CommandLineExecutionResultType executionResult = null;

        try {
            executionResult = this.runCommand(args);
        } catch (ArgumentParserException e) {
            if (parser == null) {
                throw e;
            }

            executionResult = this.handleError(parser, e, args);
        }

        switch (executionResult) {
            case Bypass:
                return;
            case Error:
                System.exit(1);
            case Executed:
                System.exit(0);
        }
    }

    public CommandLineExecutionResultType handleError(ArgumentParser parser, ArgumentParserException parentException,
            String... args) {
        if (parentException instanceof HelpScreenException) {
            return CommandLineExecutionResultType.Executed;
        }

        String command = getCommandFromArgs(args);
        if (commandName == null || !commandName.equals(command)) {
            return CommandLineExecutionResultType.Bypass;
        }

        parser.handleError(parentException);
        return CommandLineExecutionResultType.Error;
    }

    public ArgumentParser parserFor(String command, String description) {
        this.commandName = command;

        parser = ArgumentParsers.newFor(command).build()
                .defaultHelp(true)
                .description("Dump the database to a file.");
        parser.addArgument("--command")
                .type(String.class)
                .required(true)
                .choices(command)
                .help("Specify the command to launch");

        return parser;
    }

    private String getCommandFromArgs(String... args) {
        List<String> argsList = Arrays.asList(args);
        Iterator<String> it = argsList.iterator();

        while (it.hasNext()) {
            String arg = it.next();
            if (arg.startsWith("--command")) {
                String[] argAndValue = arg.split("=");
                if (argAndValue.length > 1) {
                    return argAndValue[1];
                } else if (it.hasNext()) {
                    return it.next();
                }
            }
        }

        return null;
    }
}
