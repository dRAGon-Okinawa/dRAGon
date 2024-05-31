package ai.dragon.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.dragon.enumeration.CommandLineExecutionResultType;
import ai.dragon.service.DatabaseService;
import ai.dragon.util.CommandLineRunnerWithArgumentsParser;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

@Component
public class DatabaseExportCommand extends CommandLineRunnerWithArgumentsParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DatabaseService databaseService;

    @Override
    public CommandLineExecutionResultType runCommand(String... args) throws Exception {
        ArgumentParser parser = this.parserFor("database-export", "Dump the database to a file.");
        parser.addArgument("--output")
                .type(String.class)
                .required(true)
                .help("The output file path to export the database to.");

        Namespace ns = parser.parseArgs(args);
        databaseService.exportDatabase(ns.getString("output"));
        logger.info("Database export completed to : " + ns.getString("output"));

        return CommandLineExecutionResultType.Executed;
    }
}
